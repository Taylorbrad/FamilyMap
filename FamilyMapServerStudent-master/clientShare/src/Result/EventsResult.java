package Result;

import Model.Event;

import java.util.ArrayList;

public class EventsResult {

    public ArrayList<Event> data;


    public EventsResult()
    {
        data = new ArrayList<>();
    }

    public ArrayList<Event> getData()
    {
        return data;
    }

    public void add(Event event)
    {
        data.add(event);
    }
}
