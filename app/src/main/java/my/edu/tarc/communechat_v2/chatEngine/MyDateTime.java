package my.edu.tarc.communechat_v2.chatEngine;

import android.util.Log;

import java.math.BigInteger;
import java.util.Calendar;

public class MyDateTime {

    private Calendar calendar;
    private static final int TOTAL_SECOND_IN_A_DAY = 86400;

    public MyDateTime() {
        calendar = Calendar.getInstance();
    }

    public String getDateTime() {

        int hour = calendar.get(Calendar.HOUR);

        if (hour == 0) {
            hour = 12;
        }

        return  hour + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "XX" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.YEAR);
    }

    public static String getTime(String dateTime) {
        String[] dateTimeSplit = dateTime.split("XX");

        return dateTimeSplit[0];
    }

    public String getTimeFormatSetting(String dateTime) {
        //Log.i("DATETIME", calendar.get(Calendar.DAY_OF_YEAR) + "x");
        if (!dateTime.isEmpty()) {
            String[] dateTimeSplit = dateTime.split("XX");
            String[] currentDateTimeSplit = getDateTime().split("XX");


            //Compare the date of the given date and the current date
            if (!dateTimeSplit[1].equals(currentDateTimeSplit[1])) {
                String[] dateSplit = dateTimeSplit[1].split("-");
                String[] currentDateSplit = currentDateTimeSplit[1].split("-");

                //Ensure that the year are the same
                if (dateSplit[2].equals(currentDateSplit[2])) {
                    //Ensure that is the same month
                    if (dateSplit[1].equals(currentDateSplit[1])) {
                        if ((Integer.parseInt(dateSplit[0]) + 1) == Integer.parseInt(currentDateSplit[0])) {
                            return "Yesterday";
                        } else {
                            return dateTimeSplit[1];
                        }
                    } else {
                        //Note this part i did not properly compare date
                        return dateTimeSplit[1];
                    }
                } else {
                    return dateTimeSplit[1];
                }



            } else {
                String[] timeSplit = dateTimeSplit[0].split(":");
                String[] currentTimeSplit = currentDateTimeSplit[0].split(":");

                int timeDifferences = Integer.parseInt(currentTimeSplit[0]) - Integer.parseInt(timeSplit[0]);

                if (timeDifferences > 0) {
                    if (timeDifferences > 1) {
                        if (Integer.parseInt(timeSplit[1]) > 9) {
                            return timeSplit[0] + ":" + timeSplit[1];
                        }else {
                            return timeSplit[0] + ":0" + timeSplit[1];
                        }
                    } else {
                        return timeDifferences + " Hour Ago";
                    }
                } else {
                    //Check whether the time different surpass in minute
                    if (currentTimeSplit[1].equals(timeSplit[1])) {
                        return "A Few Second Ago";
                    } else {
                        return "A Few Minute Ago";
                    }
                }

            }
        } else {
            return dateTime;
        }

    }

    public long getCurrentTimeInMillisecond() {
        return calendar.getTimeInMillis();
    }
}
