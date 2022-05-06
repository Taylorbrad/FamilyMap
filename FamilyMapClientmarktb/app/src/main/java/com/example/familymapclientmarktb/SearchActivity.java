package com.example.familymapclientmarktb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import Client.DataCache;
import Model.Event;
import Model.Person;

public class SearchActivity extends AppCompatActivity {

    private View view;
    private String searchString;

    private static final int EVENT_ITEM_VIEW_TYPE = 0;
    private static final int PERSON_ITEM_VIEW_TYPE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        DataCache cache = DataCache.getInstance();

        RecyclerView recyclerView = findViewById(R.id.RecyclerViewSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        SearchView searchBar = findViewById(R.id.SearchBar);

        searchString = "";

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //TODO do search stuff
                searchString = searchBar.getQuery().toString();
                SearchAdapter adapter;

                ArrayList<Event> emptyEventList = new ArrayList<>();
                ArrayList<Person> emptyPersonList = new ArrayList<>();

                ArrayList<Event> eventsToSearch = DataCache.getInstance().getFilteredEvents();
                ArrayList<Person> persons = DataCache.getInstance().getPersons();

                ArrayList<Event> eventsToDisplay = new ArrayList<>();
                ArrayList<Person> personsToDisplay = new ArrayList<>();

                eventsToDisplay = cache.searchEvents(eventsToSearch, searchString);
                personsToDisplay = cache.searchPersons(persons, searchString);



                if (searchString.equals(""))
                {
                    adapter = new SearchAdapter(emptyEventList,emptyPersonList);
                }
                else
                {
                    adapter = new SearchAdapter(eventsToDisplay, personsToDisplay);
                }

                recyclerView.setAdapter(adapter);

                return false;
            }
        });
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder>
    {
        ArrayList<Event> events;
        ArrayList<Person> persons;


        SearchAdapter(ArrayList<Event> events, ArrayList<Person> persons) {
            this.events = events;
            this.persons = persons;
        }

        @Override
        public int getItemViewType(int position) {
            return position < persons.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;



            if(viewType == PERSON_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.person_item, parent, false);
//                view.
            } else {
                view = getLayoutInflater().inflate(R.layout.event_item, parent, false);
            }

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if(position < persons.size()) {
                holder.bind(persons.get(position));
            } else {
                holder.bind(events.get(position - persons.size()));
            }
        }

        @Override
        public int getItemCount() {
            return events.size() + persons.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final int viewType;

        private TextView name;
        private TextView data;
        private ImageView icon;

        private Person person;
        private Event event;

        private ImageView personImageView;

        SearchViewHolder(View view, int viewType)
        {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if(viewType == EVENT_ITEM_VIEW_TYPE)
            {
                name = itemView.findViewById(R.id.ItemEventPersonsName);
                data = itemView.findViewById(R.id.ItemEventData);

                ImageView eventImageView = view.findViewById(R.id.eventImage);

                Drawable eventIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_map_marker).
                        colorRes(R.color.black).sizeDp(60);

                eventImageView.setImageDrawable(eventIcon);
            } else
            {
                name = itemView.findViewById(R.id.ItemPersonName);

                ImageView personImageView = view.findViewById(R.id.personImage);
                this.personImageView = personImageView;



            }
        }

        public void bind(Event event)
        {
            this.event = event;

            String dataString = event.getEventType() + ": "  + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";

            Person person = DataCache.getInstance().getPersonByID(event.getPersonID());

            name.setText(person.getFirstName() + " " + person.getLastName());
            data.setText(dataString);
        }
        public void bind(Person person)
        {
            this.person = person;

            Drawable personIcon = person.getGender().toLowerCase().equals("m") ? new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_male).colorRes(R.color.male_icon).sizeDp(60) :
                    new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_female).colorRes(R.color.female_icon).sizeDp(60);

            name.setText(person.getFirstName() + " " + person.getLastName());

            this.personImageView.setImageDrawable(personIcon);
        }

        @Override
        public void onClick(View view)
        {
            if(viewType == EVENT_ITEM_VIEW_TYPE) {

                DataCache.getInstance().setEventCentered(event);
                Intent eventIntent = new Intent(getApplicationContext(), EventActivity.class);
                startActivity(eventIntent);
            } else {
                DataCache.getInstance().setPersonCentered(person);
                Intent personIntent = new Intent(getApplicationContext(), PersonActivity.class);
                startActivity(personIntent);
            }
        }

    }


}