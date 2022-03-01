package com.alten.altenws.business;

import com.alten.altencore.dao.GenericDAO;
import com.alten.altencore.model.id.ReservationId;
import com.alten.altencore.model.Reservation;
import com.alten.altenws.exception.DateCannotBePastException;
import com.alten.altenws.exception.UsernameCannotBeBlankException;
import com.alten.altenws.exception.FromDateAndToDateCannotBeEqualException;
import com.alten.altenws.exception.ToDateBestBeBeforeFromDateException;
import com.alten.altenws.exception.PersistenceException;
import com.alten.altenws.exception.ReservationIsTooAheadInTimeException;
import com.alten.altenws.exception.ReservationStartsTomorrowException;
import com.alten.altenws.exception.StayCannotBeLongerThan3DaysException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.chop;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author aconti
 */
public class AltenWSService {

    /**
     *
     * @param from check-in date
     * @param to check-out date
     * @return true if the room is available, false is the room is not available
     */
    public boolean doCheckAvailability(Date from, Date to) {
        GenericDAO<ReservationId, Reservation> reservationDao = getReservationDao();
        List<Date> dateRange = getDateRange(from, to, 3);
        Session s = reservationDao.openSession();

        List<Reservation> reservationList;
        try {
            String hql = getCheckAvailabilityHQL(dateRange.size());
            Query query = s.createQuery(hql);
            for (int i = 0; i < dateRange.size(); i++) {
                query.setParameter("dateParam" + i, dateRange.get(i));
            }
            reservationList = query.list();
        } finally {
            reservationDao.closeSession(s);
        }

        return reservationList.isEmpty();
    }

    /**
     *
     * @param from check-in date
     * @param to check-out date
     * @return the code of the reservation that has been placed
     */
    public String doPlaceReservation(Date from, Date to) {
        GenericDAO<ReservationId, Reservation> reservationDao = getReservationDao();
        List<Date> dateRange = getDateRange(from, to, 3);
        Session s = reservationDao.openSession();
        Transaction t = s.beginTransaction();
        String code = getCode();
        try {
            for (Date nth : dateRange) {
                Reservation reservation = getNewReservation();
                ReservationId id = getNewReservationId();
                id.setCode(code);
                id.setTakenDate(nth);
                reservation.setId(id);
                reservationDao.save(reservation, s);
            }
            t.commit();
        } catch (Throwable th) {
            t.rollback();
            throw new PersistenceException();
        } finally {
            reservationDao.closeSession(s);
        }
        return code;
    }

    /**
     *
     * @param code the code of the reservation that has to be cancelled
     */
    public void doCancelReservation(String code) {
        GenericDAO<ReservationId, Reservation> reservationDao = getReservationDao();
        Session s = reservationDao.openSession();
        List<Reservation> reservationList;
        try {
            String hql = getReservationHQL();
            Query query = s.createQuery(hql);
            query.setParameter("codeParam", code);
            reservationList = query.list();
        } finally {
            reservationDao.closeSession(s);
        }

        s = reservationDao.openSession();
        Transaction t = s.beginTransaction();
        try {
            for (Reservation nth : reservationList) {
                reservationDao.remove(nth, s);
            }
            t.commit();
        } catch (Throwable th) {
            t.rollback();
            throw new PersistenceException();
        } finally {
            reservationDao.closeSession(s);
        }
    }

    /**
     *
     * @param from check-in date
     * @param to check-out date
     * @return a list containing a date object of from and to strings
     * @throws UsernameCannotBeBlankException
     * @throws ToDateBestBeBeforeFromDateException
     * @throws ParseException
     * @throws ReservationStartsTomorrowException
     * @throws StayCannotBeLongerThan3DaysException
     * @throws ReservationIsTooAheadInTimeException
     * @throws FromDateAndToDateCannotBeEqualException
     * @throws DateCannotBePastException
     */
    public List<Date> validateFromToDate(String from, String to) throws UsernameCannotBeBlankException, ToDateBestBeBeforeFromDateException, ParseException, ReservationStartsTomorrowException, StayCannotBeLongerThan3DaysException, ReservationIsTooAheadInTimeException, FromDateAndToDateCannotBeEqualException, DateCannotBePastException {
        if (StringUtils.isBlank(from) || StringUtils.isBlank(to)) {
            throw new IllegalArgumentException("FROM date and TO date cannot be blank");
        }

        SimpleDateFormat simpleDateFormat = getSimpleDateFormat();

        Date fromDate = simpleDateFormat.parse(from);
        Date toDate = simpleDateFormat.parse(to);
        Date today = getNewDate();

        if (fromDate.compareTo(toDate) == 0) {
            throw new FromDateAndToDateCannotBeEqualException();
        }
        if (fromDate.before(today)) {
            throw new DateCannotBePastException();
        }
        if (toDate.before(today)) {
            throw new DateCannotBePastException();
        }
        if (toDate.before(fromDate)) {
            throw new ToDateBestBeBeforeFromDateException();
        }

        long daysBetween = getDaysBetween(fromDate, toDate);
        if (daysBetween > 3) {
            throw new StayCannotBeLongerThan3DaysException();
        }

        Date thirtyDaysFromToday = getDatePlusDay(today, 30);
        if (fromDate.compareTo(thirtyDaysFromToday) > 0) {
            throw new ReservationIsTooAheadInTimeException();
        }

        List<Date> output = new ArrayList<>();
        output.add(fromDate);
        output.add(toDate);
        return output;
    }

    /**
     *
     * @param dateRangeSize
     * @return the HQL statement that takes into account how many days are
     * included in the stay
     */
    protected String getCheckAvailabilityHQL(int dateRangeSize) {
        if (dateRangeSize < 1) {
            throw new IllegalArgumentException("dateRangeSize must be greater than 0");
        }
        StringBuilder output = getNewStringBuilder("FROM com.alten.altencore.model.Reservation r WHERE ");
        for (int i = 0; i < dateRangeSize; i++) {
            output.append("r.id.takenDate = :dateParam").append(String.valueOf(i)).append(" OR ");
        }
        return chop(chop(chop(output.toString())));
    }

    /**
     *
     * @param from check-in date
     * @param to check-out date
     * @param maxDays the maximum number of days the room can be booked per
     * stay. By specification, this number is fixed to 3, i put it as an
     * argument for future refactoring of the code.
     * @return a list containing all the dates object between check-in (included) to
     * check-out (excluded)
     */
    protected List<Date> getDateRange(Date from, Date to, int maxDays) {
        List<Date> output = getNewDateArrayList();
        output.add(from);
        if (getDatePlusDay(from, 1).compareTo(to) == 0) {
            return output;
        }
        for (int i = 1; i < maxDays; i++) {
            Date nthDate = Date.from(from.toInstant().plus(i, ChronoUnit.DAYS));
            if (nthDate.compareTo(to) == 0) {
                break;
            } else {
                output.add(nthDate);
            }
        }
        return output;
    }

    protected String getReservationHQL() {
        return "FROM com.alten.altencore.model.Reservation r WHERE r.id.code = :codeParam";
    }

    protected Date getDatePlusDay(Date date, int day) {
        return Date.from(date.toInstant().plus(day, ChronoUnit.DAYS));
    }

    protected String getCode() {
        //to be replaced by a better logic handled by the DB (i.e a sequence)
        return RandomStringUtils.randomAlphanumeric(10).toUpperCase();
    }

    private Date getNewDate() {
        return new Date();
    }

    protected SimpleDateFormat getSimpleDateFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        simpleDateFormat.setLenient(false);
        return simpleDateFormat;
    }

    public void validateUsername(String username) throws UsernameCannotBeBlankException {
        if (StringUtils.isBlank(username)) {
            throw new UsernameCannotBeBlankException();
        }
    }

    protected List<Date> getNewDateArrayList() {
        return new ArrayList<>();
    }

    protected StringBuilder getNewStringBuilder(String s) {
        return new StringBuilder(s);
    }

    protected long getDaysBetween(Date fromDate, Date toDate) {
        return TimeUnit.DAYS.convert(toDate.getTime() - fromDate.getTime(), TimeUnit.MILLISECONDS);
    }

    protected GenericDAO<ReservationId, Reservation> getReservationDao() {
        return new GenericDAO<>("/altencore_wizard/altencore.cfg.xml", Reservation.class);
    }

    protected Reservation getNewReservation() {
        return new Reservation();
    }

    protected ReservationId getNewReservationId() {
        return new ReservationId();
    }
}
