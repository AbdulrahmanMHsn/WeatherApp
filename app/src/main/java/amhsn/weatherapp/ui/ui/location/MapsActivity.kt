package amhsn.weatherapp.ui.ui.location

import amhsn.weatherapp.R
import amhsn.weatherapp.databinding.ActivityMapsBinding
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.MaterialSearchBar.OnSearchActionListener
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import java.util.*
import kotlin.collections.ArrayList


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var placesClient: PlacesClient? = null
    private var predictionList: List<AutocompletePrediction>? = null

    private var mLastKnownLocation: Location? = null
    private var locationCallback: LocationCallback? = null

    private val mapView: View? = null

    private val DEFAULT_ZOOM = 15f

    private lateinit var binding:ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_maps)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@MapsActivity)
        Places.initialize(this@MapsActivity, getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
        val token:AutocompleteSessionToken  = AutocompleteSessionToken.newInstance()

        binding.searchBar.setOnSearchActionListener(object : OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {}
            override fun onSearchConfirmed(text: CharSequence) {
                startSearch(text.toString(), true, null, true)
            }

            override fun onButtonClicked(buttonCode: Int) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    //opening or closing a navigation drawer
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    binding.searchBar.disableSearch()
                }
            }
        })


        binding.searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                val predictionsRequest =
                    FindAutocompletePredictionsRequest.builder()
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build()
                placesClient!!.findAutocompletePredictions(predictionsRequest)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val predictionsResponse =
                                task.result
                            if (predictionsResponse != null) {
                                predictionList =
                                    predictionsResponse.autocompletePredictions
                                val suggestionsList: MutableList<String?> =
                                    ArrayList()
                                for (i in predictionList!!.indices) {
                                    val prediction =
                                        predictionList!!.get(i)
                                    suggestionsList.add(prediction.getFullText(null).toString())
                                }
                                binding.searchBar.updateLastSuggestions(suggestionsList)
                                if (!binding.searchBar.isSuggestionsVisible) {
                                    binding.searchBar.showSuggestionsList()
                                }
                            }
                        } else {
                            Log.i("mytag", "prediction fetching task unsuccessful")
                        }
                    }
            }

            override fun afterTextChanged(s: Editable) {}
        })


        binding.searchBar.setSuggstionsClickListener(object :
            SuggestionsAdapter.OnItemViewClickListener {
            override fun OnItemDeleteListener(position: Int, v: View?) {
                TODO("Not yet implemented")
            }

            override fun OnItemClickListener(position: Int, v: View?) {
                if (position >= predictionList!!.size) {
                    return
                }
                val selectedPrediction = predictionList!![position]
                val suggestion: String =
                    binding.searchBar.getLastSuggestions().get(position).toString()
                binding.searchBar.setText(suggestion)
                Handler().postDelayed(Runnable { binding.searchBar.clearSuggestions() }, 1000)
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm != null) imm.hideSoftInputFromWindow(
                    binding.searchBar.getWindowToken(),
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
                val placeId = selectedPrediction.placeId
                val placeFields: List<Place.Field> = Arrays.asList(Place.Field.LAT_LNG)
                val fetchPlaceRequest: FetchPlaceRequest =
                    FetchPlaceRequest.builder(placeId, placeFields).build()
                placesClient!!.fetchPlace(fetchPlaceRequest)
                    .addOnSuccessListener(object : OnSuccessListener<FetchPlaceResponse?> {
                        override fun onSuccess(fetchPlaceResponse: FetchPlaceResponse?) {
                            val place: Place = fetchPlaceResponse!!.getPlace()
                            Log.i("mytag", "Place found: " + place.getName())
                            val latLngOfPlace: LatLng = place.getLatLng()!!
                            if (latLngOfPlace != null) {
                                mMap.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        latLngOfPlace,
                                        DEFAULT_ZOOM
                                    )
                                )
                            }
                        }
                    }).addOnFailureListener(object : OnFailureListener {
                       override fun onFailure(@NonNull e: Exception) {
                            if (e is ApiException) {
                                val apiException: ApiException = e as ApiException
                                apiException.printStackTrace()
                                val statusCode: Int = apiException.getStatusCode()
                                Log.i("mytag", "place not found: " + e.message)
                                Log.i("mytag", "status code: $statusCode")
                            }
                        }
                    })
            }
        })


        binding.btnFind.setOnClickListener(View.OnClickListener {
            val currentMarkerLocation = mMap.cameraPosition.target
            binding.rippleBg.startRippleAnimation()
//            Handler().postDelayed({
//                binding.rippleBg.stopRippleAnimation()
//                startActivity(Intent(this@MapsActivity, MainActivity::class.java))
//                finish()
//            }, 3000)
        })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        if (mapView != null && mapView.findViewById<View?>("1".toInt()) != null) {
            val locationButton =
                (mapView.findViewById<View>("1".toInt())
                    .parent as View).findViewById<View>("2".toInt())
            val layoutParams: RelativeLayout.LayoutParams =
                locationButton.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 40, 180)
        }

        //check if gps is enabled or not and then request user to enable it

        //check if gps is enabled or not and then request user to enable it
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(this@MapsActivity)
        val task =
            settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener(
            this@MapsActivity,
            object : OnSuccessListener<LocationSettingsResponse?>{
                override fun onSuccess(locationSettingsResponse: LocationSettingsResponse?) {
                    getDeviceLocation()
                }
            })

        task.addOnFailureListener(this@MapsActivity, object : OnFailureListener {
            override fun onFailure(@NonNull e: Exception) {
                if (e is ResolvableApiException) {
                    val resolvable: ResolvableApiException = e as ResolvableApiException
                    try {
                        resolvable.startResolutionForResult(this@MapsActivity, 51)
                    } catch (e1: IntentSender.SendIntentException) {
                        e1.printStackTrace()
                    }
                }
            }
        })

        mMap.setOnMyLocationButtonClickListener {
            if (binding.searchBar.isSuggestionsVisible()) binding.searchBar.clearSuggestions()
            if (binding.searchBar.isSearchEnabled()) binding.searchBar.disableSearch()
            false
        }

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 51) {
            if (resultCode == Activity.RESULT_OK) {
                getDeviceLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        mFusedLocationProviderClient!!.lastLocation
            .addOnCompleteListener(object : OnCompleteListener<Location?> {
                override fun onComplete(task: Task<Location?>) {
                    if (task.isSuccessful()) {
                        mLastKnownLocation = task.getResult()
                        if (mLastKnownLocation != null) {
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        mLastKnownLocation!!.getLatitude(),
                                        mLastKnownLocation!!.getLongitude()
                                    ), DEFAULT_ZOOM
                                )
                            )
                        } else {
                            val locationRequest: LocationRequest = LocationRequest.create()
                            locationRequest.setInterval(10000)
                            locationRequest.setFastestInterval(5000)
                            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            locationCallback = object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult?) {
                                    super.onLocationResult(locationResult)
                                    if (locationResult == null) {
                                        return
                                    }
                                    mLastKnownLocation = locationResult.getLastLocation()
                                    mMap.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(
                                                mLastKnownLocation!!.getLatitude(),
                                                mLastKnownLocation!!.getLongitude()
                                            ), DEFAULT_ZOOM
                                        )
                                    )
                                    mFusedLocationProviderClient!!.removeLocationUpdates(
                                        locationCallback
                                    )
                                }
                            }
                            mFusedLocationProviderClient!!.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                null
                            )
                        }
                    } else {
//                        Toast.makeText(
//                            this@MapActivity,
//                            "unable to get last location",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }
                }
            })
    }
}