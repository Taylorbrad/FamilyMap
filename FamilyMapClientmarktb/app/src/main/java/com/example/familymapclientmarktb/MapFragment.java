package com.example.familymapclientmarktb;

//import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import Client.DataCache;
import Model.Event;
import Model.Person;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private View view;
    private ArrayList<Polyline> lifeLinesDrawn = new ArrayList<>();
    private ArrayList<Polyline> spouseLinesDrawn = new ArrayList<>();
    //private ArrayList<Polyline> famTreeLinesDrawn = new ArrayList<>();
    private ArrayList<Polyline> linesDrawn = new ArrayList<>();
    private ArrayList<Marker> markersDrawn = new ArrayList<>();
    private ArrayList<Event> eventsToDraw = new ArrayList<>();
    ArrayList<Event> userEvents = new ArrayList<>();
    private SharedPreferences preferences;
    private ArrayList<Float> colors = new ArrayList<>();
    HashMap<String, Float> colorMap = new HashMap<>();
    private int colorIter = 0;

    private Marker selectedMarker;
    private Event sMarkerTag;

    ArrayList<Event> fatherSideEvents = new ArrayList<>();
    ArrayList<Event> motherSideEvents = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        this.view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        {
            colors.add(BitmapDescriptorFactory.HUE_AZURE);
            colors.add(BitmapDescriptorFactory.HUE_GREEN);
            colors.add(BitmapDescriptorFactory.HUE_BLUE);
            colors.add(BitmapDescriptorFactory.HUE_ORANGE);
            colors.add(BitmapDescriptorFactory.HUE_ROSE);
            colors.add(BitmapDescriptorFactory.HUE_CYAN);
            colors.add(BitmapDescriptorFactory.HUE_MAGENTA);
            colors.add(BitmapDescriptorFactory.HUE_RED);
            colors.add(BitmapDescriptorFactory.HUE_VIOLET);
            colors.add(BitmapDescriptorFactory.HUE_YELLOW);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setHasOptionsMenu(true);

        TextView eventDataText = view.findViewById(R.id.mapEventData);
        TextView eventNameText = view.findViewById(R.id.mapFirstLastName);
        ImageView eventImage = view.findViewById(R.id.genderImageView);

        eventDataText.setOnClickListener(personActivityListener);
        eventNameText.setOnClickListener(personActivityListener);
        eventImage.setOnClickListener(personActivityListener);

        return view;
    }

    View.OnClickListener personActivityListener =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent personIntent = new Intent(getActivity(), PersonActivity.class);
            startActivity(personIntent);
        }
    };
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu.findItem(R.id.settingsMenuItem) != null)
        {
            menu.findItem(R.id.settingsMenuItem).setVisible(true);
        }
        if (menu.findItem(R.id.searchMenuItem) != null)
        {
            menu.findItem(R.id.searchMenuItem).setVisible(true);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        DataCache.getInstance().getFilteredPersons().clear();

        HashSet<Person> filteredPersons = new HashSet<>();
        filteredPersons.add(new Person("","","","","","","",""));

        ArrayList<Event> eventsDrawn = new ArrayList<>();

        markersDrawn = new ArrayList<>();
        DataCache cache = DataCache.getInstance();

        motherSideEvents = DataCache.getInstance().getMotherSideEvents();
        fatherSideEvents = DataCache.getInstance().getFatherSideEvents();

        map = googleMap;
        map.setOnMapLoadedCallback(this);


        userEvents = cache.getPersonLifeEventsByID().get(cache.getPersonID());

        for (int i = 0; i < userEvents.size(); ++i)
        {
            eventsToDraw.add(userEvents.get(i));
        }

        String spouseID = cache.getPersonByID(cache.getPersonID()).getSpouseID();
        ArrayList<Event> userSpouseEvents = cache.getPersonLifeEventsByID().get(spouseID);

        filteredPersons.add(cache.getPersonByID(cache.getPersonID()));
        filteredPersons.add(cache.getPersonByID(spouseID));

        if (userSpouseEvents == null)
        {
            userSpouseEvents = new ArrayList<>();
        }

        for (int i = 0; i < userSpouseEvents.size(); ++i)
        {
            eventsToDraw.add(userSpouseEvents.get(i));
        }

        if (preferences.getBoolean("fatherSide", false))
        {
            for(int i = 0; i < fatherSideEvents.size(); ++i)
            {
                eventsToDraw.add(fatherSideEvents.get(i));
                filteredPersons.add(cache.getPersonByID(fatherSideEvents.get(i).getPersonID()));
            }
        }

        if (preferences.getBoolean("motherSide", false))
        {
            for(int i = 0; i < motherSideEvents.size(); ++i)
            {
                eventsToDraw.add(motherSideEvents.get(i));
                filteredPersons.add(cache.getPersonByID(motherSideEvents.get(i).getPersonID()));
            }
        }

        for(int i = 0; i < eventsToDraw.size(); ++i)
        {
            Event event = eventsToDraw.get(i);
            float newColor = 0;

            String eventType = event.eventType.toLowerCase(Locale.ROOT);

            if (colorMap.get(eventType) == null && colorIter < 9)
            {
                colorMap.put(eventType, colors.get(colorIter));
                newColor = colors.get(colorIter);
                colorIter++;
            }
            else if (colorMap.get(eventType) != null)
            {
                newColor = colorMap.get(eventType);
            }
            else
            {
                newColor = colors.get(9);
            }
            //
            String male = preferences.getBoolean("maleEvents", false) ? "m" : "";
            String female = preferences.getBoolean("femaleEvents", false) ? "f" : "";

            if (cache.getPersonByID(event.getPersonID()).getGender().equals(male) || cache.getPersonByID(event.getPersonID()).getGender().equals(female))
            {
                LatLng latlng = new LatLng(event.latitude, event.longitude);
                MarkerOptions newMarker = new MarkerOptions();
                newMarker.position(latlng);
                newMarker.title(event.city);
                newMarker.icon(BitmapDescriptorFactory.defaultMarker(newColor));

                Marker marker = map.addMarker(newMarker);

                marker.setTag(event);

                markersDrawn.add(marker);
                eventsDrawn.add(event);

            }
            else
            {
                filteredPersons.remove(cache.getPersonByID(event.getPersonID()));
            }
        }


        cache.setFilteredPersons(filteredPersons);

        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(10,10)));

        map.setOnMarkerClickListener(listener);

        DataCache.getInstance().setFilteredEvents(eventsDrawn);


        if (cache.getEventCentered() != null)
        {

            Event event = cache.getEventCentered();

            String eventType = event.getEventType();

            LatLng latlng = new LatLng(event.latitude, event.longitude);
            MarkerOptions newMarker = new MarkerOptions();
            newMarker.position(latlng);
            newMarker.title(event.city);

            if (colorMap.get(eventType) != null)
            {
                newMarker.icon(BitmapDescriptorFactory.defaultMarker(colorMap.get(eventType)));
            }
            else
            {
                newMarker.icon(BitmapDescriptorFactory.defaultMarker(colors.get(9)));
            }

            Marker marker = map.addMarker(newMarker);

            marker.setTag(event);

            selectedMarker = marker;
            sMarkerTag = event;
        }

        if (selectedMarker != null)
        {
            selectedMarker.setTag(sMarkerTag);
            listener.onMarkerClick(selectedMarker);
        }
    }

    public Polyline drawLine(Event startEvent, Event endEvent, int googleColor, float width)
    {
        LatLng start = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
        LatLng end = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());


        PolylineOptions options = new PolylineOptions()
                .add(start)
                .add(end)
                .color(googleColor)
                .width(width);

        Polyline line = map.addPolyline(options);
        line.setTag(options);

        return line;
    }

    public void drawFamilyLines(Person person, Event personsEvent, int width)
    {
        DataCache cache = DataCache.getInstance();

        Person father = cache.getPersonByID(person.getFatherID());
        Person mother = cache.getPersonByID(person.getMotherID());

        if (father != null)
        {
            ArrayList<Event> fathersEvents = cache.getPersonLifeEventsByID().get(father.getPersonID());

            if (fathersEvents != null)
            {
                int drawLine = 0;
                for(int i = 0; i < markersDrawn.size(); ++i)
                {
                    if (markersDrawn.get(i).getTag().equals(fathersEvents.get(0)))
                    {
                        drawLine++;
                    }
                    if (markersDrawn.get(i).getTag().equals(personsEvent))
                    {
                        drawLine++;
                    }
                }

                if (drawLine == 2)
                {
                    Polyline line = drawLine(personsEvent, fathersEvents.get(0), Color.GREEN, width);
                    linesDrawn.add(line);
                    drawFamilyLines(father, fathersEvents.get(0), width/2);
                }
            }
        }

        if (mother != null)
        {
            ArrayList<Event> mothersEvents = cache.getPersonLifeEventsByID().get(mother.getPersonID());

            if (mothersEvents != null)
            {
                int drawLine = 0;
                for(int i = 0; i < markersDrawn.size(); ++i)
                {
                    if (markersDrawn.get(i).getTag().equals(mothersEvents.get(0)))
                    {
                        drawLine++;
                    }
                    if (markersDrawn.get(i).getTag().equals(personsEvent))
                    {
                        drawLine++;
                    }
                }
                if (drawLine == 2)
                {
                    Polyline line = drawLine(personsEvent, mothersEvents.get(0), Color.GREEN, width);
                    linesDrawn.add(line);
                    drawFamilyLines(mother, mothersEvents.get(0), width-5);
                }

            }
        }

    }


    @Override
    public void onResume(){
        super.onResume();
        markersDrawn = new ArrayList<>();

        ArrayList<Event> eventsDrawn = new ArrayList<>();
        DataCache cache = DataCache.getInstance();
        cache.getFilteredPersons().clear();

        HashSet<Person> filteredPersons = new HashSet<>();
        filteredPersons.add(new Person("","","","","","","",""));

        if (map != null)
        {
            map.clear();
        }

        eventsToDraw.clear();

        String spouseID = cache.getPersonByID(cache.getPersonID()).getSpouseID();
        ArrayList<Event> userSpouseEvents = cache.getPersonLifeEventsByID().get(spouseID);

        filteredPersons.add(cache.getPersonByID(cache.getPersonID()));
        filteredPersons.add(cache.getPersonByID(spouseID));

        if (userSpouseEvents == null)
        {
            userSpouseEvents = new ArrayList<>();
        }

        for (int i = 0; i < userSpouseEvents.size(); ++i)
        {

            if (map != null)
            {
                eventsToDraw.add(userSpouseEvents.get(i));
            }
        }

        for (int i = 0; i < userEvents.size(); ++i)
        {
           eventsToDraw.add(userEvents.get(i));
        }

        if (preferences.getBoolean("fatherSide", false))
        {
            for(int i = 0; i < fatherSideEvents.size(); ++i)
            {
                eventsToDraw.add(fatherSideEvents.get(i));
                filteredPersons.add(cache.getPersonByID(fatherSideEvents.get(i).getPersonID()));
            }

        }

        //colorIter = 0;
        if (preferences.getBoolean("motherSide", false))
        {
            for(int i = 0; i < motherSideEvents.size(); ++i)
            {
                eventsToDraw.add(motherSideEvents.get(i));
                filteredPersons.add(cache.getPersonByID(motherSideEvents.get(i).getPersonID()));
            }
        }


        for(int i = 0; i < eventsToDraw.size(); ++i)
        {
            Event event = eventsToDraw.get(i);
            float newColor = 0;
            String eventType = event.eventType.toLowerCase(Locale.ROOT);

            if (colorMap.get(eventType) == null && colorIter < 9)
            {
                colorMap.put(eventType, colors.get(colorIter));
                newColor = colors.get(colorIter);
                colorIter++;
            }
            else if (colorMap.get(eventType) != null)
            {
                newColor = colorMap.get(eventType);
            }
            else
            {
                newColor = colors.get(9);
            }
            //
            String male = preferences.getBoolean("maleEvents", false) ? "m" : "";
            String female = preferences.getBoolean("femaleEvents", false) ? "f" : "";


            if (cache.getPersonByID(event.getPersonID()).getGender().equals(male) || cache.getPersonByID(event.getPersonID()).getGender().equals(female))
            {
                LatLng latlng = new LatLng(event.latitude, event.longitude);
                MarkerOptions newMarker = new MarkerOptions();
                newMarker.position(latlng);
                newMarker.title(event.city);
                newMarker.icon(BitmapDescriptorFactory.defaultMarker(newColor));

                Marker marker;

                marker = map.addMarker(newMarker);


                marker.setTag(event);

                markersDrawn.add(marker);
                eventsDrawn.add(event);


            }
            else
            {
                filteredPersons.remove(cache.getPersonByID(event.getPersonID()));
            }

            cache.setFilteredPersons(filteredPersons);

        }
        DataCache.getInstance().setFilteredEvents(eventsDrawn);



        if (selectedMarker != null)
        {
            selectedMarker.setTag(sMarkerTag);
            listener.onMarkerClick(selectedMarker);
        }

    }

    com.google.android.gms.maps.GoogleMap.OnMarkerClickListener listener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(@NonNull Marker marker) {
            DataCache cache = DataCache.getInstance();
            selectedMarker = marker;
            sMarkerTag = (Event)marker.getTag();

            map.animateCamera(CameraUpdateFactory.newLatLng(selectedMarker.getPosition()));

            for(int i = 0; i < linesDrawn.size(); ++i)
            {
                linesDrawn.get(i).remove();
            }

            linesDrawn.clear();

            Event markerEvent = ((Event)marker.getTag());

            TextView nameData = view.findViewById(R.id.mapFirstLastName);
            TextView eventData = view.findViewById(R.id.mapEventData);
            ImageView genderImage = view.findViewById(R.id.genderImageView);



            if (markerEvent != null)
            {
                Person markerPerson = cache.getPersonByID(markerEvent.getPersonID());
                cache.setPersonCentered(markerPerson);

                nameData.setText(markerPerson.getFirstName() + " " + markerPerson.getLastName());
                eventData.setText(markerEvent.getEventType() + ": " + markerEvent.getCity() + ", " + markerEvent.getCountry() + " (" + markerEvent.getYear() + ")");

                Drawable maleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                        colorRes(R.color.male_icon).sizeDp(60);

                Drawable femaleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                        colorRes(R.color.female_icon).sizeDp(60);

                Drawable eventIcon = (markerPerson.getGender().equals("m") ? maleIcon : femaleIcon);

                genderImage.setImageDrawable(eventIcon);


                ArrayList<Event> personsLifeEvents = cache.getPersonLifeEventsByID().get(markerPerson.getPersonID());

                if (preferences.getBoolean("lifeLines", false)) {
                    for (int i = 1; i < personsLifeEvents.size(); ++i) //Draw LifeEvents Lines
                    {
                        int drawLine = 0;
                        Event event1 = personsLifeEvents.get(i - 1);
                        Event event2 = personsLifeEvents.get(i);

                        for (int y = 0; y < markersDrawn.size(); ++y)
                        {
                            if (((Event)markersDrawn.get(y).getTag()).equals(event1))
                            {
                                drawLine++;
                            }
                            if (((Event)markersDrawn.get(y).getTag()).equals(event2))
                            {
                                drawLine++;
                            }
                        }

                        if (drawLine == 2)
                        {
                            Polyline line = drawLine(event1, event2, Color.RED, 10);
                            linesDrawn.add(line);
                        }
                    }
                }

                if (preferences.getBoolean("spouseLines", false)) {

                    Event spouseBirth = cache.getSpouseBirthEventByID(markerPerson.getPersonID());
                    if (spouseBirth != null)
                    {
                        int drawLine = 0;
                        for (int y = 0; y < markersDrawn.size(); ++y)
                        {
                            if (((Event)markersDrawn.get(y).getTag()).equals(spouseBirth))
                            {
                                drawLine++;
                            }
                        }

                        if (drawLine == 1)
                        {
                            Polyline line = drawLine(markerEvent, spouseBirth, Color.BLUE, 10);
                            linesDrawn.add(line);
                        }

                    }
                }


                if (preferences.getBoolean("treeLines", false))
                {
                    drawFamilyLines(markerPerson, markerEvent, 20);
                }


                if (markerPerson.getGender().equals("m")) {
                    nameData.setTextColor(Color.parseColor("#000080"));
                    eventData.setTextColor(Color.parseColor("#000080"));
                } else {
                    nameData.setTextColor(Color.parseColor("#fc03f0"));
                    eventData.setTextColor(Color.parseColor("#fc03f0"));
                }

            }
            return false;

        }
    };
    @Override
    public void onMapLoaded() {
    }
}