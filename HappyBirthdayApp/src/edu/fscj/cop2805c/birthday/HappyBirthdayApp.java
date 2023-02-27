// HappyBirthdayApp.java
// D. Singletary
// 1/29/23
// wish multiple users a happy birthday

// D. Singletary
// 2/26/23
// Added Stream and localization code

package edu.fscj.cop2805c.birthday;

import edu.fscj.cop2805c.dispatch.Dispatcher;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Stream;


// utility class with constant list of desired timezones (US/)
class DesiredTimeZones {
    public static final ArrayList<String> ZONE_LIST = new ArrayList<>();

    public DesiredTimeZones() {
        String[] availableTimezones = TimeZone.getAvailableIDs();
        for (String s : availableTimezones) {
            if (s.length() >= 3 && s.substring(0, 3).equals("US/")) {
                ZONE_LIST.add(s);
            }
        }
    }

    // show the list of zones as a numeric menu
    public void showMenu() {
        int menuCount = 1;
        for (String s : ZONE_LIST)
            System.out.println(menuCount++ + ". " + s);
    }
}

// main mpplication class
public class HappyBirthdayApp implements BirthdayGreeter, Dispatcher<BirthdayCard>  {
    private ArrayList<User> birthdays = new ArrayList<>();

    // Use a Queue<LinkedList> to act as message queue for the dispatcher
    private Queue<BirthdayCard> queue = new LinkedList<BirthdayCard>();
    private Stream<BirthdayCard> stream = queue.stream();

    public HappyBirthdayApp() { }

    // dispatch the card using the dispatcher
    public void dispatch(BirthdayCard bc) {
        this.queue.add(bc);
    }

    // send the card
    public void sendCard(BirthdayCard bc) {
        // dispatch the card
        // dispatch(bc);
        // show an alternative dispatch using a lambda
        Dispatcher<BirthdayCard> d = (c)-> {
            this.queue.add(c);
//            System.out.println("current queue length is " + this.queue.size());
//            System.out.println(c);
//            System.out.println("sending a birthday card to " +
//                    c.getUser().getEmail() + "\n");
        };
        d.dispatch(bc);
    }

    public BirthdayCard buildCard(User u, String msg) {
        final String NEWLINE = "\n";

        // get the widest line and number of lines in the message
        int longest = getLongest(msg);

        // need to center lines
        // dashes on top (header) and bottom (footer)
        // vertical bars on the sides
        // |-----------------------|
        // | longest line in group |
        // |      other lines      |
        // |-----------------------|
        //
        // pad with an extra space if the length is odd

        int numDashes = (longest + 2) + (longest % 2);  // % 2 to pad if odd length
        char[] dashes = new char[numDashes];  // header and footer
        char[] spaces = new char[numDashes];  // body lines
        Arrays.fill(dashes, '-');
        Arrays.fill(spaces, ' ');
        String headerFooter = "|" + new String(dashes) + "|\n";
        String spacesStr = "|" + new String(spaces) + "|\n";

        // start the message with the header
        String message = headerFooter;

        // split the message into separate strings
        String[] splitStr = msg.split(NEWLINE);
        for (String s : splitStr) {
            String line = spacesStr;  // start with all spaces

            // create a StringBuilder with all spaces,
            // then replace some spaces with the centered string
            StringBuilder buildLine = new StringBuilder(spacesStr);

            // start at the middle minus half the length of the string (0-based)
            int start = (spacesStr.length() / 2) - (s.length() / 2);
            // end at the starting index plus the length of the string
            int end = start + s.length();
            /// replace the spaces and create a String, then append
            buildLine.replace(start, end, s);
            line = new String(buildLine);
            message += line;
        }
        // append the footer
        message += headerFooter;
        //System.out.println(card); // debug
        BirthdayCard card = new BirthdayCard(u, message);
        return card;
    }

    // given a String containing a (possibly) multi-line message,
    // split the lines, find the longest line, and return its length
    public int getLongest(String s) {
        final String NEWLINE = "\n";
        int maxLength = 0;
        String[] splitStr = s.split(NEWLINE);
        for (String line : splitStr)
            if (line.length() > maxLength)
                maxLength = line.length();
        return maxLength;
    }

    // show prompt msg with no newline
    public static void prompt(String msg) {
        System.out.print(msg + ": ");
    }

    // compare current month and day to user's data
    // to see if it is their birthday
    public boolean isBirthday(User u) {
        boolean result = false;

        LocalDate today = LocalDate.now();
        if (today.getMonth() == u.getBirthday().getMonth() &&
                today.getDayOfMonth() == u.getBirthday().getDayOfMonth())
            result = true;

        return result;
    }

    // add multiple birthdays
    public void addBirthdays(User... users) {
        for (User u : users) {
            birthdays.add(u);
        }
    }

    // main program
    public static void main(String[] args) {

        HappyBirthdayApp hba = new HappyBirthdayApp();

        // create a list of desired timezones to use for our app,
        // we'll use the US/ zones
        DesiredTimeZones dzt = new DesiredTimeZones();

        // test the varargs method by creating multiple birthdays and adding them.
        // names were generated using the random name generator at
        //     http://random-name-generator.info/
        // be sure to test the positive case (today is someone's birthday
        // as well as negative)

        // use current date for testing, adjust where necessary
        ZonedDateTime currentDate = ZonedDateTime.now();

        // negative test
        User u1 = new User("Dianne", "Romero", "Dianne.Romero@email.test",
                new Locale("en"), currentDate.minusDays(1));

        // positive tests
        // test with odd length full name and english locale
        User u2 = new User("Sally", "Ride", "Sally.Ride@email.test",
                new Locale("en"), currentDate);

        // test french locale
        User u3 = new User("René", "Descartes", "René.Descartes@email.test",
                new Locale("fr"), currentDate);

        // test with even length full name and german locale
        User u4 = new User("Johannes", "Brahms", "Johannes.Brahms@email.test",
                new Locale("de"), currentDate);

        // test chinese locale
        User u5 = new User("Charles", "Kao", "Charles.Kao@email.test",
                new Locale("zh"), currentDate);

        hba.addBirthdays(u1, u2, u3, u4, u5);

        // show the birthdays
        if (!hba.birthdays.isEmpty()) {
            System.out.println("Here are the users:");
            for (User u : hba.birthdays) {
                System.out.println(u.getName());
                // see if today is their birthday
                // if not, show sorry message
                if (!hba.isBirthday(u))
                    System.out.println("Sorry, today is not their birthday.");
                // otherwise build the card
                else {
                    String msg = "";
                    try {

                        // load the property and create the localized greeting
                        ResourceBundle res = ResourceBundle.getBundle(
                                "edu.fscj.cop2805c.birthday.Birthday", u.getLocale());
                        String happyBirthday = res.getString("HappyBirthday");

                        // format and display the date
                        DateTimeFormatter formatter =
                                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
                        formatter =
                                formatter.localizedBy(u.getLocale());
                        msg = u.getBirthday().format(formatter) + "\n";

                        // add the localized greeting
                        msg += happyBirthday + " " + u.getName() + "\n" +
                                BirthdayCard.WISHES;
                    } catch (java.util.MissingResourceException e) {
                        System.err.println(e);
                        msg = "Happy Birthday, " + u.getName() + "\n" +
                                BirthdayCard.WISHES;
                    }
                    BirthdayCard bc = hba.buildCard(u, msg);
                    hba.sendCard(bc);
                }
            }
        }
        System.out.println("starting forEach");
        hba.stream.forEach(System.out::print);
    }
}