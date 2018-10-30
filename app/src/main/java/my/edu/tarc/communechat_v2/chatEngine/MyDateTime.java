package my.edu.tarc.communechat_v2.chatEngine;

import android.util.Log;

import java.util.Calendar;

public class MyDateTime {

    private Calendar calendar;

    public MyDateTime() {
        calendar = Calendar.getInstance();
    }

    public String getDateTime() {
        return calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + "XX" + (calendar.get(Calendar.DAY_OF_MONTH)+1) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR);
    }

    public static String getTime(String dateTime) {
        String[] dateTimeSplit = dateTime.split("XX");
        //Log.i("Z", dateTime + " ZZ " + dateTimeSplit[0] + " zz " + dateTimeSplit[1]);
        return dateTimeSplit[0];
    }

    public String getTimeFormatSetting(String dateTime) {
        //Log.i("DATETIME", calendar.get(Calendar.DAY_OF_YEAR) + "x");
        if (!dateTime.isEmpty()) {
            String[] dateTimeSplit = dateTime.split("XX");
            String[] currentDateTimeSplit = getDateTime().split("XX");

            //Compare the date of the given date and the current date
            if (dateTimeSplit[1].equals(currentDateTimeSplit[1])) {
                String[] dateSplit = dateTimeSplit[1].split("-");
                String[] currentDateSplit = currentDateTimeSplit[1].split("-");

                //Ensure that the year are the same
                if (dateSplit[2].equals(currentDateSplit[2])) {
                    if (dateSplit[1].equals(currentDateSplit[1])) {
                        if ((Integer.parseInt(dateSplit[0]) + 1) == Integer.parseInt(currentDateSplit[1])) {
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

    //public String compareLatestTime

}
