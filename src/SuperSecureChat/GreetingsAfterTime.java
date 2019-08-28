package SuperSecureChat;

import java.util.Calendar;

public class GreetingsAfterTime {

    public void gettimebycalendar() {
        Calendar calendar = Calendar.getInstance();
        int timeofday = calendar.get(Calendar.HOUR_OF_DAY);

        if (timeofday > 0 && timeofday < 12) {
            System.out.println("Good Morning");
        } else if (timeofday >= 12 && timeofday < 16) {
            System.out.println("Good Afternoon");
        } else if (timeofday >= 16 && timeofday < 21) {
            System.out.println("Good Evening");
        } else if (timeofday >= 21 && timeofday < 24) {
            System.out.println("Good Night");
        }
    }
}
