package com.example.voiceassistant2;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.voiceassistant2.Weather.ForecastToString;
import com.example.voiceassistant2.digit.ConvertToString;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AI {

    HashMap<String, String> QeustionAndAnswers;
    private String time, dayOfWeek, day, summer;
    private int daysToSummer;
    SimpleDateFormat format;
    Date dateOne, dateTwo;
    String[] answers;

    public AI ()
    {
        format = new SimpleDateFormat("dd.MM.yyyy");
        dateOne = null;
        dateTwo = null;
        summer = "01.06.2020";
        day = new SimpleDateFormat ("dd.MM.yyyy").format(new Date());
        time = new SimpleDateFormat ("hh:mm").format(new Date());
        dayOfWeek = new SimpleDateFormat ("EEEE").format(new Date());
        daysToSummer = DaysToSummer();
        answers = new String[]{"Вопрос понял, думаю..."};
        QeustionAndAnswers = new HashMap<>();
        QeustionAndAnswers.put("привет","Привет");
        QeustionAndAnswers.put("приветик","Привет");
        QeustionAndAnswers.put("ку","Привет");
        QeustionAndAnswers.put("приветули","Привет");
        QeustionAndAnswers.put("здорова","Привет");
        QeustionAndAnswers.put("как дела","Неплохо");
        QeustionAndAnswers.put("чем занимаешься","Отвечаю на вопросы");
        QeustionAndAnswers.put("чем маешься","Отвечаю на вопросы");
        QeustionAndAnswers.put("а чем занимаешься","Отвечаю на вопросы");
        QeustionAndAnswers.put("какой сегодня день",day);
        QeustionAndAnswers.put("который час",time);
        QeustionAndAnswers.put("какой день недели",dayOfWeek);
        QeustionAndAnswers.put("сколько осталось до лета","До лета осталось "+String.valueOf(daysToSummer) +" дня или дней");
    }
    public String getDegreeEnding(int b) {
        int a = Math.abs(b);
        if(a>=5 && a <=20)
            return " градусов ";
        if(a%10==0 || a%10 >=5 && a%10 <=9)
            return " градусов ";
        if(a%10 == 1)
            return " градус ";

        return " градуса ";
    }
    private int DaysToSummer ()
    {

        long difference=0;
        try {
            dateOne = format.parse(summer);
            dateTwo = format.parse(day);
            difference = dateOne.getTime()-dateTwo.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Перевод количества дней между датами из миллисекунд в дни
        int days =  (int)(difference / (24 * 60 * 60 * 1000)); // миллисекунды / (24ч * 60мин * 60сек * 1000мс)
        // Вывод разницы между датами в днях на экран
        return  days;
    }
    private static DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                    "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        }
    };
    private String getDate(String s)
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", myDateFormatSymbols );
        String holiday = null;

        Pattern datePattern = Pattern.compile("(\\d{1,2}.\\d{1,2}.\\d{4})"); // задаем текст запроса
        Matcher matcher = datePattern.matcher(s);
        if (matcher.find())
        {
            String finder = matcher.group(1);
            try {
                Date finder_result = format.parse(finder);
                holiday = dateFormat.format(finder_result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (s.contains("вчера"))
        {
            c.add(Calendar.DAY_OF_YEAR, -1);
            holiday = dateFormat.format(c.getTime());
        }
        else if (s.contains("сегодня"))
        {
            holiday = dateFormat.format(c.getTime());
        }
        else if (s.contains("завтра"))
        {
            c.add(Calendar.DAY_OF_YEAR, 1);
            holiday = dateFormat.format(c.getTime());
        }


        return holiday;
    }
    public void getAnswer(String Question, final Consumer<String> callback) {
        Question = Question.toLowerCase();

        Pattern cityPattern = Pattern.compile("погода в городе (\\p{L}+)",Pattern.CASE_INSENSITIVE);
        Matcher matcher = cityPattern.matcher(Question);
        Pattern convertPattern = Pattern.compile("перевести число (\\d+)"); // задаем текст запроса
        Matcher convertMatcher = convertPattern.matcher(Question);
        Pattern celebratePattern = Pattern.compile("праздник", Pattern.CASE_INSENSITIVE); // задаем текст запроса
        Matcher celebrateMatcher = celebratePattern.matcher(Question);
        if (matcher.find()) {
            final String cityName = matcher.group(1);
            answers[0] = "Не знаю я, какая у вас там погода";
            ForecastToString.getForecast(cityName, new Consumer<String>() {
                @Override
                public void accept(String s) {
                    if (s != null) {
                        callback.accept(String.join(", ", s));
                    } else {
                        callback.accept(String.join(", ", answers[0]));
                    }
                }
            });


        } else if (convertMatcher.find()) { // перевод числа из цифр в буквы
            final String digit = convertMatcher.group(1);
            answers[0] = "Не знаю, что за число";

            ConvertToString.getConvert(digit, new Consumer<String>() {
                @Override
                public void accept(String s) {
                    if (s != null) {
                        callback.accept(String.join(", ", s));
                    } else {
                        callback.accept(String.join(", ", answers[0]));
                    }
                }
            });
        } else if (celebrateMatcher.find()) // праздник в заданную дату
        {
            answers[0] = "Не могу дать ответ";
            ParsingHtmlService phs = new ParsingHtmlService();
            String findDate = getDate(Question);
            if (findDate != null)
            {
                String[] strings = findDate.split(",");
                final String[] s = {" "};
                Observable.fromCallable(() -> {
                    for (int i = 0; i < strings.length; i++)
                    {
                        try
                        {
                            s[0] += strings[i] + ": " + phs.getHoliday(strings[i]) + "\n";
                        } catch (IOException e) {
                            s[0] = "Что-то пошло не так";
                        }
                    }
                    return s[0];
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((result) -> {
                            callback.accept(String.join(", ", s[0]));
                        });
            }
            else callback.accept(String.join(", ", answers[0]));
        }
        else {
            for (HashMap.Entry<String, String> entry : QeustionAndAnswers.entrySet()) {
                if (Question.contains(entry.getKey())) {
                    answers[0] = entry.getValue();
                    break;
                }
            }
            callback.accept(String.join(", ", answers[0]));
        }

    }
}
