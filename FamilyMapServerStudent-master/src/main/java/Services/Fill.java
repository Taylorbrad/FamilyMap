package Services;

import DAO.*;
import Model.Event;
import Model.Location;
import Model.Person;
import Model.User;
import Result.FillResult;
import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Random;

public class Fill extends BaseService{

    private Database db;
    private Connection conn;

    private Gson gson;

    PersonDAO pDAO;
    UserDAO uDAO;
    EventDAO eDAO;

    ArrayList<String> fNames;
    ArrayList<String> mNames;
    ArrayList<String> sNames;
    Location locations;



    int originalGenerations;

    //ArrayList<String>
    /**
     * creates new fill object
     */
    public Fill() throws DataAccessException, IOException
    {
        gson = new Gson();
        db = new Database("jdbc:sqlite:familymap.sqlite");
        conn = db.getConnection();

        //
        // generate lists of names and locations based on json files provided.
        //
        File fnJSON = new File("json/fnames.json");
        File mnJSON = new File("json/mnames.json");
        File snJSON = new File("json/snames.json");
        File locJSON = new File("json/locations.json");

        if (fnJSON.isFile())
        {
            String file1 = Files.readString(fnJSON.toPath());
            String file2 = Files.readString(mnJSON.toPath());
            String file3 = Files.readString(snJSON.toPath());
            String file4 = Files.readString(locJSON.toPath());

            fNames = gson.fromJson(file1, ArrayList.class);
            mNames = gson.fromJson(file2, ArrayList.class);
            sNames = gson.fromJson(file3, ArrayList.class);
            locations = gson.fromJson(file4, Location.class);
        }

        pDAO = new PersonDAO(conn);
        uDAO = new UserDAO(conn);
        eDAO = new EventDAO(conn);
    }
    /**
     * sends the fill request to the server. returns a FillResult object.
     * @param inUsername
     * @param generations
     * @return
     */


    public FillResult fill(String inUsername, String personID, int generations) throws DataAccessException
    {
        boolean isFirst = false;

        Random rand = new Random();

        //
        // Generate these random locations and names for events and parents we will be adding to the family tree.
        //
        Location randLoc1 = locations.getLocations().get(rand.nextInt(locations.getLocations().size()));
        Location randLoc2 = locations.getLocations().get(rand.nextInt(locations.getLocations().size()));
        Location randLoc3 = locations.getLocations().get(rand.nextInt(locations.getLocations().size()));

        String randMName = mNames.get(rand.nextInt(mNames.size()));
        String randFName = fNames.get(rand.nextInt(fNames.size()));;
        String randSName = sNames.get(rand.nextInt(sNames.size()));;

        if (personID.equals(""))
        {
            //
            // This essentially does anything we need to on the first pass through. This clears the DB
            //
            GeneralDAO dao = new GeneralDAO(conn);
            User userTemp = uDAO.find(inUsername);
            personID = userTemp.getPersonID();
            isFirst = true;
            //dao.customUpdateSQL("DELETE FROM Persons WHERE AssociatedUsername = \'username\' AND spouseID IS NOT NULL;");
            //dao.customUpdateSQL("DELETE FROM Events WHERE AssociatedUsername = \'username\';");
            originalGenerations = generations;
        }

        Person person = pDAO.find(personID);

        String birthID = generateUUID();
        String marriageIDF = generateUUID();
        String marriageIDM= generateUUID();
        String deathID = generateUUID();

        int birthYear = 1970 - (20 * (originalGenerations - generations));


        //
        //generate and insert events for the base user (we keep track of this with the isFirst boolean.
        //

        Event birth = new Event(birthID, inUsername, personID, randLoc1.getLatitude(),randLoc1.getLongitude(),randLoc1.getCountry(), randLoc1.getCity(), "Birth", birthYear);
        Event death = new Event(deathID, inUsername, personID, randLoc3.getLatitude(), randLoc3.getLongitude(), randLoc3.getCountry(), randLoc3.getCity(), "Death", birthYear + 70);

        eDAO.insert(birth);
        if (!isFirst)
        {
            eDAO.insert(death);
        }

        //System.out.println("not here");
        if(generations > 0)
        {

            //
            // generate ID's, people, and events for parents of the passed in user.
            //
            String fatherID = generateUUID();
            String motherID = generateUUID();

            Person father = new Person(fatherID, inUsername, randMName, randSName, "m", null,null,motherID);
            Person mother = new Person(motherID, inUsername, randFName, randSName, "f", null,null,fatherID);

            Event marriageFather = new Event(marriageIDF, inUsername, fatherID, randLoc2.getLatitude(), randLoc2.getLongitude(), randLoc2.getCountry(), randLoc2.getCity(), "Marriage", birthYear - 2);
            Event marriageMother = new Event(marriageIDM, inUsername, motherID, randLoc2.getLatitude(), randLoc2.getLongitude(), randLoc2.getCountry(), randLoc2.getCity(), "Marriage", birthYear - 2);

            pDAO.addParentsToPerson(personID, fatherID, motherID);

            pDAO.insert(father);
            pDAO.insert(mother);

            eDAO.insert(marriageFather);
            eDAO.insert(marriageMother);


            //
            //recursively call fill() with a decremented generations parameter, to continue
            // generating parents for the parents, and so on and so forth.
            //
            fill(inUsername, fatherID, (generations - 1));
            fill(inUsername, motherID, (generations - 1));
        }

        if (isFirst)
        {
            db.closeConnection(true);
        }

        return null;
    }
}
