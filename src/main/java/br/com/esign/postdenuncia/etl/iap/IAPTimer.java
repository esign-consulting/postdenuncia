package br.com.esign.postdenuncia.etl.iap;

import java.util.Calendar;
import java.util.Date;

import br.com.esign.postdenuncia.etl.FonteTask;
import br.com.esign.postdenuncia.etl.FonteTimer;

public class IAPTimer extends FonteTimer {

    public IAPTimer(String name) {
        super(name);
    }

    @Override
    protected FonteTask getFonteTask() {
        return new IAPTask();
    }

    @Override
    protected Date getFirstTime() {
        Calendar calendar = Calendar.getInstance();
        int m = calendar.get(Calendar.MINUTE);
        if (m > 45) {
            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }
        int newM = (m < 15) ? 15 : (m < 30) ? 30 : (m < 45) ? 45 : 0;
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