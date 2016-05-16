package br.com.esign.postdenuncia.etl.cetesb;

import java.util.Calendar;
import java.util.Date;

import br.com.esign.postdenuncia.etl.FonteTask;
import br.com.esign.postdenuncia.etl.FonteTimer;

public class CETESBTimer extends FonteTimer {

    public CETESBTimer(String name) {
        super(name);
    }

    @Override
    protected FonteTask getFonteTask() {
        return new CETESBTask();
    }

    @Override
    protected Date getFirstTime() {
        Calendar calendar = Calendar.getInstance();
        int m = calendar.get(Calendar.MINUTE);
        if (m > 50) {
            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }
        int newM = (m < 5) ? 5 : (m < 20) ? 20 : (m < 35) ? 35 : (m < 50) ? 50 : 5;
        calendar.set(Calendar.MINUTE, newM);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Override
    protected long getPeriod() {
        return 15 * 60 * 1000;
    }

}