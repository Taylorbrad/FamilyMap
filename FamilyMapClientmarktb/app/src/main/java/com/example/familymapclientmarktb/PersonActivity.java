package com.example.familymapclientmarktb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;
import java.util.List;

import Client.DataCache;
import Model.Event;
import Model.Person;

public class PersonActivity extends AppCompatActivity {


    Person personToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Iconify.with(new FontAwesomeModule());
        this.personToDisplay = DataCache.getInstance().getPersonCentered();

        TextView personsFirstName = findViewById(R.id.personsFirstName);
        TextView personsLastName = findViewById(R.id.personsLastName);
        TextView personsGender = findViewById(R.id.personsGender);

        personsFirstName.setText(personToDisplay.getFirstName());
        personsLastName.setText(personToDisplay.getLastName());
        personsGender.setText(personToDisplay.getGender().equals("m") ? "Male" : "Female");

        ArrayList<Event> personLifeEvents = DataCache.getInstance().getPersonLifeEventsByID().get(personToDisplay.getPersonID());
        ArrayList<Person> familyMembers = DataCache.getInstance().getPersonsFamilyMembersByID(personToDisplay.getPersonID());

        if (personToDisplay.getGender().equals("m"))
        {
            if(!preferences.getBoolean("maleEvents", false))
            {
                personLifeEvents = new ArrayList<>();
            }
        }
        else
        {
            if(!preferences.getBoolean("femaleEvents", false))
            {
                personLifeEvents = new ArrayList<>();
            }
        }




        ExpandableListView expandableListView = findViewById(R.id.expandableListView);



        expandableListView.setAdapter(new ExpandableListAdapter(familyMembers, personLifeEvents));

        DataCache.getInstance().clearFamilyMembers();

    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int PERSON_GROUP_POSITION = 0;
        private static final int EVENT_GROUP_POSITION = 1;

        private final ArrayList<Person> persons;
        private final ArrayList<Event> events;

        ExpandableListAdapter(ArrayList<Person> persons, ArrayList<Event> events) {
            this.persons = persons;
            this.events = events;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    return persons.size();
                case EVENT_GROUP_POSITION:
                    return events.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            // Not used
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // Not used
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case PERSON_GROUP_POSITION:
                    titleView.setText("Persons: ");
                    break;
                case EVENT_GROUP_POSITION:
                    titleView.setText("Events: ");
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case PERSON_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_item, parent, false);
                    initializePersonView(itemView, childPosition);
                    break;
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_item, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializePersonView(View personItemView, final int childPosition) {
            Person person = persons.get(childPosition);

            TextView personRelationshipView = personItemView.findViewById(R.id.ItemPersonRelationship);
            TextView personNameView = personItemView.findViewById(R.id.ItemPersonName);
            personNameView.setText(person.getFirstName() + " " + person.getLastName());
            personRelationshipView.setText(person.getAssociatedUsername());

            ImageView personImageView = personItemView.findViewById(R.id.personImage);

            Drawable personIcon = person.getGender().toLowerCase().equals("m") ? new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_male).colorRes(R.color.male_icon).sizeDp(60) :
                    new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_female).colorRes(R.color.female_icon).sizeDp(60);

            personImageView.setImageDrawable(personIcon);

            personItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataCache.getInstance().setPersonCentered(person);
                    Intent personIntent = new Intent(getApplicationContext(), PersonActivity.class);
                    startActivity(personIntent);
                }
            });
        }

        private void initializeEventView(View eventItemView, final int childPosition) {
            Event event = events.get(childPosition);
            Person person = DataCache.getInstance().getPersonByID(event.getPersonID());

            if (person != null && event != null)
            {
                TextView eventDataView = eventItemView.findViewById(R.id.ItemEventData);
                String eventDataString = event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
                eventDataView.setText(eventDataString);


                TextView eventNameView = eventItemView.findViewById(R.id.ItemEventPersonsName);
                eventNameView.setText(person.getFirstName() + " " + person.getLastName());

                ImageView eventImageView = eventItemView.findViewById(R.id.eventImage);

                Drawable eventIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_map_marker).
                        colorRes(R.color.black).sizeDp(60);

                eventImageView.setImageDrawable(eventIcon);
            }

            eventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataCache.getInstance().setEventCentered(event);
                    Intent eventIntent = new Intent(getApplicationContext(), EventActivity.class);
                    startActivity(eventIntent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}