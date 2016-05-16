package br.com.esign.postdenuncia.etl.iema;

import java.util.Calendar;
import java.util.Date;

import br.com.esign.postdenuncia.etl.FonteTask;
import br.com.esign.postdenuncia.etl.FonteTimer;

public class IEMATimer extends FonteTimer {

    public IEMATimer(String name) {
        super(name);
    }

    @Override
    protected FonteTask getFonteTask() {
        return new IEMATask();
    }

    @Override
    protected Date getFirstTime() {
        Calendar calendar = Calendar.getInstance();
        int m = calendar.get(Calendar.MINUTE);
        if (m > 25) {
            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }
        calendar.set(Calendar.MINUTE, 25);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Override
    protected long getPeriod() {
        return 60 * 60 * 1000;
    }

}