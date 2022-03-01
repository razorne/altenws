package com.alten.altenws.business;

import com.alten.altencore.dao.GenericDAO;
import com.alten.altencore.model.Reservation;
import com.alten.altencore.model.id.ReservationId;
import com.alten.altenws.exception.DateCannotBePastException;
import com.alten.altenws.exception.FromDateAndToDateCannotBeEqualException;
import com.alten.altenws.exception.PersistenceException;
import com.alten.altenws.exception.ReservationIsTooAheadInTimeException;
import com.alten.altenws.exception.ReservationStartsTomorrowException;
import com.alten.altenws.exception.StayCannotBeLongerThan3DaysException;
import com.alten.altenws.exception.ToDateBestBeBeforeFromDateException;
import com.alten.altenws.exception.UsernameCannotBeBlankException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mockito;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 *
 * @author aconti
 */
public class AltenWSServiceTest {

    @Test
    public void testing_getReservationHQL() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String expectedOutput = "FROM com.alten.altencore.model.Reservation r WHERE r.id.code = :codeParam";
        //definition

        //stubbing
        //stubbing
        //do
        String output = altenWSService.getReservationHQL();
        Assertions.assertThat(output).isEqualTo(expectedOutput);
        //do
    }

    @Test
    public void testing_getCode() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        //definition

        //stubbing
        //stubbing
        //do
        String output = altenWSService.getCode();
        Assertions.assertThat(output).isNotBlank();
        Assertions.assertThat(output.length() == 10).isTrue();
        //do
    }

    @Test
    public void testing_getSimpleDateFormat() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        //definition

        //stubbing
        //stubbing
        //do
        SimpleDateFormat output = altenWSService.getSimpleDateFormat();
        Assertions.assertThat(output.toPattern()).isEqualTo("yyyyMMdd");
        Assertions.assertThat(output.isLenient()).isFalse();
        //do
    }

    @Test
    public void testing_validateUsername() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String username = "asd";
        //definition

        //stubbing
        //stubbing
        //do
        Assertions.assertThatCode(() -> altenWSService.validateUsername(username))
                .doesNotThrowAnyException();
        //do
    }

    @Test
    public void testing_validateUsername_when_username_is_null() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String username = null;
        //definition

        //stubbing
        //stubbing
        //do
        Assertions.assertThatExceptionOfType(UsernameCannotBeBlankException.class)
                .isThrownBy(() -> altenWSService.validateUsername(username));
        //do
    }

    @Test
    public void testing_validateUsername_when_username_is_empty() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String username = "";
        //definition

        //stubbing
        //stubbing
        //do
        Assertions.assertThatExceptionOfType(UsernameCannotBeBlankException.class)
                .isThrownBy(() -> altenWSService.validateUsername(username));
        //do
    }

    @Test
    public void testing_getDateRange_today_todayPlus1() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        Date today = new Date();
        Date to = altenWSService.getDatePlusDay(today, 1);
        //definition

        //stubbing
        //stubbing
        //do
        List<Date> output = altenWSService.getDateRange(today, to, 3);
        Assertions.assertThat(output).hasSize(1);
        Assertions.assertThat(output.get(0).compareTo(today) == 0).isTrue();
        //do
    }

    @Test
    public void testing_getDateRange_today_todayPlus2() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        Date today = new Date();
        Date to = altenWSService.getDatePlusDay(today, 2);
        //definition

        //stubbing
        //stubbing
        //do
        List<Date> output = altenWSService.getDateRange(today, to, 3);
        Assertions.assertThat(output).hasSize(2);
        Assertions.assertThat(output.get(0).compareTo(today) == 0).isTrue();
        Assertions.assertThat(output.get(1).compareTo(altenWSService.getDatePlusDay(today, 1)) == 0).isTrue();
        //do
    }

    @Test
    public void testing_getDateRange_today_todayPlus3() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        Date today = new Date();
        Date to = altenWSService.getDatePlusDay(today, 3);
        //definition

        //stubbing
        //stubbing
        //do
        List<Date> output = altenWSService.getDateRange(today, to, 3);
        Assertions.assertThat(output).hasSize(3);
        Assertions.assertThat(output.get(0).compareTo(today) == 0).isTrue();
        Assertions.assertThat(output.get(1).compareTo(altenWSService.getDatePlusDay(today, 1)) == 0).isTrue();
        Assertions.assertThat(output.get(2).compareTo(altenWSService.getDatePlusDay(today, 2)) == 0).isTrue();
        //do
    }

    @Test
    public void testing_getCheckAvailabilityHQL_when_dateRange_is_less_than_1() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        int dateRange = 0;
        //definition

        //stubbing
        //stubbing
        //do
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> altenWSService.getCheckAvailabilityHQL(dateRange))
                .withMessage("dateRangeSize must be greater than 0");
        //do
    }

    @Test
    public void testing_getCheckAvailabilityHQL_when_dateRange_is_1() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String expectedOutput = "FROM com.alten.altencore.model.Reservation r WHERE r.id.takenDate = :dateParam0 ";
        int dateRange = 1;
        //definition

        //stubbing
        //stubbing
        //do
        String output = altenWSService.getCheckAvailabilityHQL(dateRange);
        Assertions.assertThat(output).isEqualTo(expectedOutput);
        //do
    }

    @Test
    public void testing_validateFromToDate_when_fromDate_isNull() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String from = "";
        String to = "20210101";
        //definition

        //stubbing
        //stubbing
        //do
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> altenWSService.validateFromToDate(from, to))
                .withMessage("FROM date and TO date cannot be blank");
        //do
    }

    @Test
    public void testing_validateFromToDate_when_toDate_isNull() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String from = "20210101";
        String to = "";
        //definition

        //stubbing
        //stubbing
        //do
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> altenWSService.validateFromToDate(from, to))
                .withMessage("FROM date and TO date cannot be blank");
        //do
    }

    @Test
    public void testing_validateFromToDate_when_fromDate_is_not_parseable() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String from = "2021AAAAAA";
        String to = "20210101";
        //definition

        //stubbing
        //stubbing
        //do
        Assertions.assertThatExceptionOfType(ParseException.class)
                .isThrownBy(() -> altenWSService.validateFromToDate(from, to));
        //do
    }

    @Test
    public void testing_validateFromToDate_when_toDate_is_not_parseable() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String from = "20210101";
        String to = "2021AAAAAA";
        //definition

        //stubbing
        //stubbing
        //do
        Assertions.assertThatExceptionOfType(ParseException.class)
                .isThrownBy(() -> altenWSService.validateFromToDate(from, to));
        //do
    }

    @Test
    public void testing_validateFromToDate_when_fromDate_and_toDate_are_equal() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String from = "20210101";
        String to = "20210101";
        //definition

        //stubbing
        //stubbing
        //do
        Assertions.assertThatExceptionOfType(FromDateAndToDateCannotBeEqualException.class)
                .isThrownBy(() -> altenWSService.validateFromToDate(from, to));
        //do
    }

    @Test
    public void testing_validateFromToDate_when_fromDate_isPast() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String from = "20210101";
        String to = "30000101";
        //definition

        //stubbing
        //stubbing
        //do
        Assertions.assertThatExceptionOfType(DateCannotBePastException.class)
                .isThrownBy(() -> altenWSService.validateFromToDate(from, to));
        //do
    }

    @Test
    public void testing_validateFromToDate_when_toDate_isPast() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String from = "30000101";
        String to = "20210101";
        //definition

        //stubbing
        //stubbing
        //do
        Assertions.assertThatExceptionOfType(DateCannotBePastException.class)
                .isThrownBy(() -> altenWSService.validateFromToDate(from, to));
        //do
    }

    @Test
    public void testing_validateFromToDate_when_toDate_isBefore_fromDate() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String from = "30000201";
        String to = "30000101";
        //definition

        //stubbing
        //stubbing
        //do
        Assertions.assertThatExceptionOfType(ToDateBestBeBeforeFromDateException.class)
                .isThrownBy(() -> altenWSService.validateFromToDate(from, to));
        //do
    }

    @Test
    public void testing_validateFromToDate_when_daysBetween_is_greater_than_3() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String from = "30000101";
        String to = "30000105";
        //definition

        //stubbing
        //stubbing
        //do
        Assertions.assertThatExceptionOfType(StayCannotBeLongerThan3DaysException.class)
                .isThrownBy(() -> altenWSService.validateFromToDate(from, to));
        //do
    }

    @Test
    public void testing_validateFromToDate_when_fromDate_is_too_ahead_in_time() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String from = "30000101";
        String to = "30000104";
        //definition

        //stubbing
        //stubbing
        //do
        Assertions.assertThatExceptionOfType(ReservationIsTooAheadInTimeException.class)
                .isThrownBy(() -> altenWSService.validateFromToDate(from, to));
        //do
    }

    @Test
    public void testing_validateFromToDate() throws UsernameCannotBeBlankException, ToDateBestBeBeforeFromDateException, ParseException, ReservationStartsTomorrowException, StayCannotBeLongerThan3DaysException, ReservationIsTooAheadInTimeException, FromDateAndToDateCannotBeEqualException, DateCannotBePastException {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        String from = "20220310";
        String to = "20220311";
        List<Date> expectedOutput = new ArrayList<>();
        expectedOutput.add(altenWSService.getSimpleDateFormat().parse(from));
        expectedOutput.add(altenWSService.getSimpleDateFormat().parse(to));
        //definition

        //stubbing
        //stubbing
        //do
        List<Date> output = altenWSService.validateFromToDate(from, to);
        Assertions.assertThat(output).hasSize(expectedOutput.size());
        Assertions.assertThat(output.get(0).compareTo(expectedOutput.get(0)));
        Assertions.assertThat(output.get(1).compareTo(expectedOutput.get(1)));
        //do
    }

    @Test
    public void testing_doCancelReservation() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        AltenWSService altenWSServiceSpy = Mockito.spy(altenWSService);
        GenericDAO<ReservationId, Reservation> reservationDao = Mockito.mock(GenericDAO.class);
        Session session = Mockito.mock(Session.class);
        Transaction transaction = Mockito.mock(Transaction.class);
        Query query = Mockito.mock(Query.class);
        List<Reservation> reservationList = new ArrayList<>();
        Reservation reservation = new Reservation();
        reservationList.add(reservation);
        //definition

        //stubbing
        Mockito.doReturn(reservationDao).when(altenWSServiceSpy).getReservationDao();
        Mockito.doReturn(session).when(reservationDao).openSession();
        Mockito.doReturn(query).when(session).createQuery(anyString());
        Mockito.doReturn(reservationList).when(query).list();
        Mockito.doReturn(transaction).when(session).beginTransaction();
        //stubbing

        //do
        altenWSServiceSpy.doCancelReservation("code");
        //do

        //verify
        verify(reservationDao, times(2)).openSession();
        verify(reservationDao, times(2)).closeSession(any(Session.class));
        verify(reservationDao, times(1)).remove(any(Reservation.class), any(Session.class));
        verify(transaction, times(1)).commit();
        verify(query, atLeast(1)).setParameter(anyString(), anyString());
        //verify
    }

    @Test
    public void testing_doCancelReservation_rollback_is_invoked_when_some_problem_occurs() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        AltenWSService altenWSServiceSpy = Mockito.spy(altenWSService);
        GenericDAO<ReservationId, Reservation> reservationDao = Mockito.mock(GenericDAO.class);
        Session session = Mockito.mock(Session.class);
        Transaction transaction = Mockito.mock(Transaction.class);
        Query query = Mockito.mock(Query.class);
        List<Reservation> reservationList = new ArrayList<>();
        Reservation reservation = new Reservation();
        reservationList.add(reservation);
        //definition

        //stubbing
        Mockito.doReturn(reservationDao).when(altenWSServiceSpy).getReservationDao();
        Mockito.doReturn(session).when(reservationDao).openSession();
        Mockito.doReturn(query).when(session).createQuery(anyString());
        Mockito.doReturn(reservationList).when(query).list();
        Mockito.doReturn(transaction).when(session).beginTransaction();
        Mockito.doThrow(PersistenceException.class).when(transaction).commit();
        //stubbing

        //do
        try {
            altenWSServiceSpy.doCancelReservation("code");
        } catch (PersistenceException pe) {

        }
        //do

        //verify
        verify(reservationDao, times(2)).openSession();
        verify(reservationDao, times(2)).closeSession(any(Session.class));
        verify(reservationDao, times(1)).remove(any(Reservation.class), any(Session.class));
        verify(transaction, times(1)).commit();
        verify(transaction, times(1)).rollback();
        verify(query, atLeast(1)).setParameter(anyString(), anyString());
        //verify
    }

    @Test
    public void testing_doPlaceReservation() throws UsernameCannotBeBlankException, ToDateBestBeBeforeFromDateException, ParseException, ReservationStartsTomorrowException, StayCannotBeLongerThan3DaysException, ReservationIsTooAheadInTimeException, FromDateAndToDateCannotBeEqualException, DateCannotBePastException {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        AltenWSService altenWSServiceSpy = Mockito.spy(altenWSService);
        GenericDAO<ReservationId, Reservation> reservationDao = Mockito.mock(GenericDAO.class);
        Session session = Mockito.mock(Session.class);
        Transaction transaction = Mockito.mock(Transaction.class);
        Reservation reservation = Mockito.mock(Reservation.class);
        Date from = new Date();
        Date to = altenWSServiceSpy.getDatePlusDay(from, 3);
        ReservationId reservationId = Mockito.mock(ReservationId.class);
        //definition

        //stubbing
        Mockito.doReturn(reservationDao).when(altenWSServiceSpy).getReservationDao();
        Mockito.doReturn(session).when(reservationDao).openSession();
        Mockito.doReturn(transaction).when(session).beginTransaction();
        Mockito.doReturn(reservation).when(altenWSServiceSpy).getNewReservation();
        Mockito.doReturn(reservationId).when(altenWSServiceSpy).getNewReservationId();
        //stubbing

        //do
        altenWSServiceSpy.doPlaceReservation(from, to);
        //do

        //verify
        verify(reservationDao, times(1)).openSession();
        verify(reservationDao, times(1)).closeSession(any(Session.class));
        verify(transaction, times(1)).commit();
        verify(altenWSServiceSpy, times(1)).getCode();
        verify(altenWSServiceSpy, times(3)).getNewReservation();
        verify(altenWSServiceSpy, times(3)).getNewReservationId();
        verify(reservationId, times(3)).setCode(anyString());
        verify(reservation, times(3)).setId(ArgumentMatchers.any(ReservationId.class));
        verify(reservationDao, times(3)).save(ArgumentMatchers.any(Reservation.class), ArgumentMatchers.any(Session.class));
        //verify
    }

    @Test
    public void testing_doPlaceReservation_rollback_is_invoked_when_some_problem_occurs() throws UsernameCannotBeBlankException, ToDateBestBeBeforeFromDateException, ParseException, ReservationStartsTomorrowException, StayCannotBeLongerThan3DaysException, ReservationIsTooAheadInTimeException, FromDateAndToDateCannotBeEqualException, DateCannotBePastException {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        AltenWSService altenWSServiceSpy = Mockito.spy(altenWSService);
        GenericDAO<ReservationId, Reservation> reservationDao = Mockito.mock(GenericDAO.class);
        Session session = Mockito.mock(Session.class);
        Transaction transaction = Mockito.mock(Transaction.class);
        Reservation reservation = Mockito.mock(Reservation.class);
        Date from = new Date();
        Date to = altenWSServiceSpy.getDatePlusDay(from, 3);
        ReservationId reservationId = Mockito.mock(ReservationId.class);
        //definition

        //stubbing
        Mockito.doReturn(reservationDao).when(altenWSServiceSpy).getReservationDao();
        Mockito.doReturn(session).when(reservationDao).openSession();
        Mockito.doReturn(transaction).when(session).beginTransaction();
        Mockito.doReturn(reservation).when(altenWSServiceSpy).getNewReservation();
        Mockito.doReturn(reservationId).when(altenWSServiceSpy).getNewReservationId();
        Mockito.doThrow(PersistenceException.class).when(transaction).commit();
        //stubbing

        //do
        try {
            altenWSServiceSpy.doPlaceReservation(from, to);
        } catch (PersistenceException pe) {

        }
        //do

        //verify
        verify(reservationDao, times(1)).openSession();
        verify(reservationDao, times(1)).closeSession(any(Session.class));
        verify(transaction, times(1)).commit();
        verify(transaction, times(1)).rollback();
        verify(altenWSServiceSpy, times(1)).getCode();
        verify(altenWSServiceSpy, times(3)).getNewReservation();
        verify(altenWSServiceSpy, times(3)).getNewReservationId();
        verify(reservationId, times(3)).setCode(anyString());
        verify(reservation, times(3)).setId(ArgumentMatchers.any(ReservationId.class));
        verify(reservationDao, times(3)).save(ArgumentMatchers.any(Reservation.class), ArgumentMatchers.any(Session.class));
        //verify
    }

    @Test
    public void testing_doCheckAvailability() {
        //definition
        AltenWSService altenWSService = new AltenWSService();
        AltenWSService altenWSServiceSpy = Mockito.spy(altenWSService);
        GenericDAO<ReservationId, Reservation> reservationDao = Mockito.mock(GenericDAO.class);
        Session session = Mockito.mock(Session.class);
        Transaction transaction = Mockito.mock(Transaction.class);
        Query query = Mockito.mock(Query.class);
        List<Reservation> reservationList = new ArrayList<>();
        Reservation reservation = new Reservation();
        reservationList.add(reservation);
        Date from = new Date();
        Date to = altenWSServiceSpy.getDatePlusDay(from, 3);
        //definition

        //stubbing
        Mockito.doReturn(reservationDao).when(altenWSServiceSpy).getReservationDao();
        Mockito.doReturn(session).when(reservationDao).openSession();
        Mockito.doReturn(query).when(session).createQuery(anyString());
        Mockito.doReturn(reservationList).when(query).list();
        Mockito.doReturn(transaction).when(session).beginTransaction();
        //stubbing

        //do
        altenWSServiceSpy.doCheckAvailability(from, to);
        //do

        //verify
        verify(reservationDao, times(1)).openSession();
        verify(reservationDao, times(1)).closeSession(any(Session.class));
        verify(altenWSServiceSpy, times(1)).getCheckAvailabilityHQL(3);
        verify(query, times(1)).list();
        verify(query, atLeast(1)).setParameter(anyString(), any(Date.class));
        //verify
    }
}
