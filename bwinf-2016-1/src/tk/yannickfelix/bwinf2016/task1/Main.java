package tk.yannickfelix.bwinf2016.task1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Aufgabe 1 Bundeswettbewerb Informatik 2016/17
 *
 * @author Yannick Félix
 * @version 1.0
 * @since 27.09.2016
 */
public class Main {
    GregorianCalendar gr, jl;
    BufferedReader reader;

    public Main() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        //Set up Calendars
        gr = new GregorianCalendar();
        jl = new GregorianCalendar();
        //Make GregorianCalendar JulianCalendar
        jl.setGregorianChange(new Date(Long.MAX_VALUE));

        //Set up values
        boolean found = false;
        System.out.println("Christmas-and-Easter-on-the-same-day-Calculator");
        System.out.println("by Yannick Félix");
        System.out.println();
        //Ask for a year to start
        System.out.print("Enter a (positive) year to begin: ");
        int year;
        try {
            year = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            //Or use 2016 as default
            year = 2016;
        }
        //Ask for mode
        System.out.println("=========================================\n1: Orthodox Christmas == Catholic/Protestant Easter\n2: Catholic/Protestant Christmas == Orthodox Easter");
        System.out.println("Enter Mode: ");
        int mode;
        try {
            mode = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            //Or use 1 as default
            mode = 1;
        }

        while(!found) {
            String jlDate, grDate;
            if(mode == 1) {
                //Calculate Easter dates and insert into calendar
                gr.setTimeInMillis(getEasterSundayDateGr(year));
                jl.setTimeInMillis(getEasterSundayDateGr(year));

                //Make Strings of the dates for easy comparism
                jlDate = jl.get(Calendar.DAY_OF_MONTH)+"."+(jl.get(Calendar.MONTH)+1)+"."+jl.get(Calendar.YEAR);
                grDate = gr.get(Calendar.DAY_OF_MONTH)+"."+(gr.get(Calendar.MONTH)+1)+"."+gr.get(Calendar.YEAR);

                //Test for orthodox christmas
                found = jlDate.contains("25.12.");
            } else {
                //Calculate Easter dates and insert into calendar
                gr.setTimeInMillis(getEasterSundayDateJl(year));
                jl.setTimeInMillis(getEasterSundayDateJl(year));

                //Make Strings of the dates for easy comparism
                jlDate = jl.get(Calendar.DAY_OF_MONTH)+"."+(jl.get(Calendar.MONTH)+1)+"."+jl.get(Calendar.YEAR);
                grDate = gr.get(Calendar.DAY_OF_MONTH)+"."+(gr.get(Calendar.MONTH)+1)+"."+gr.get(Calendar.YEAR);

                //Test for catholic christmas
                found = grDate.contains("25.12.");
            }
            System.out.print("=> Year: "+year);
            if(found) {
                //If found, print dates
                System.out.println(" found!");
                System.out.println("=========================================");
                System.out.println("Gregorian Calendar Date: "+grDate+"\nJulian Calendar Date:    "+jlDate);
                System.out.println("=========================================");
                //And ask to continue search
                System.out.println("Continue? ");
                try {
                    //If user types "n", exit program
                    //Else continue search
                    found = reader.readLine().equalsIgnoreCase("n");
                } catch (IOException e) {}
            } else {
                //Else say no.
                System.out.println(" no.");
            }
            year++;
        }
    }

    /**
     * This method calculates the Easter Sunday Date for the given year (Gregorian Calendar)<br/>
     * <br/>
     * Source: Gaußsche Osterformel <a href="https://de.wikipedia.org/wiki/Gau%C3%9Fsche_Osterformel">(Wikipedia)</a>
     *
     * @param year Year to calculate
     * @return Time in Milliseconds
     */
    public static long getEasterSundayDateGr(int year) {
        int a = year % 19,
                b = year % 4,
                c = year % 7,
                k = year / 100,
                p = (8*k + 13) / 25,
                q = k / 4,
                M = (15 + k - p - q) % 30,
                d = (19*a + M) % 30,
                N = (4 + k - q) % 7,
                e = (2*b + 4*c + 6*d + N) % 7,
                D = 22 + d + e,
                m = 3;
        if(D>31) { D -= 31; m = 4; }

        return new GregorianCalendar(year, m-1, D).getTimeInMillis();
    }

    /**
     * This method calculates the Easter Sunday Date for the given year (Julian Calendar)<br/>
     * <br/>
     * Source: Gaußsche Osterformel <a href="https://de.wikipedia.org/wiki/Gau%C3%9Fsche_Osterformel">(Wikipedia)</a>
     *
     * @param year Year to calculate
     * @return Time in Milliseconds
     */
    public static long getEasterSundayDateJl(int year) {
        int a = year % 19,
                b = year % 4,
                c = year % 7,
                k = year / 100,
                M = 15,
                d = (19*a + M) % 30,
                N = 6,
                e = (2*b + 4*c + 6*d + N) % 7,
                D = 22 + d + e,
                m = 3;
        if(D>31) { D -= 31; m = 4; }
        GregorianCalendar jl = new GregorianCalendar();
        jl.setGregorianChange(new Date(Long.MAX_VALUE));
        jl.set(year, m-1, D);

        return jl.getTimeInMillis();
    }

    public static void main(String[] args) {
        Main m = new Main();
    }
}
