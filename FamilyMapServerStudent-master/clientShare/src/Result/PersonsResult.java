package Result;

import Model.Person;

import java.util.ArrayList;

public class PersonsResult {

    public ArrayList<Person> data;


    public PersonsResult()
    {
        data = new ArrayList<>();
    }

    public ArrayList<Person> getData()
    {
        return data;
    }

    public void add(Person person)
    {
        data.add(person);
    }
}