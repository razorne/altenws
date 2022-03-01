package com.alten.altenws.controller;

import com.alten.altenws.business.AltenWSService;
import com.alten.altenws.exception.DateCannotBePastException;
import com.alten.altenws.exception.UsernameCannotBeBlankException;
import com.alten.altenws.exception.FromDateAndToDateCannotBeEqualException;
import com.alten.altenws.exception.ToDateBestBeBeforeFromDateException;
import com.alten.altenws.exception.ReservationIsTooAheadInTimeException;
import com.alten.altenws.exception.ReservationStartsTomorrowException;
import com.alten.altenws.exception.StayCannotBeLongerThan3DaysException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author aconti
 */
@Controller
//the request mapping at this level means that every following url will have "/api" as prefix
@RequestMapping(value = "/api")
public class AltenWSController {

    @Autowired
    private AltenWSService altenWSService;

    @RequestMapping(value = "check/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> check(HttpServletRequest request, HttpServletResponse response) {
        try {
            String from = request.getHeader("from");
            String to = request.getHeader("to");
            List<Date> dateList = altenWSService.validateFromToDate(from, to);
            boolean available = altenWSService.doCheckAvailability(dateList.get(0), dateList.get(1));
            return new ResponseEntity<>(available + "", HttpStatus.OK);
        } catch (RuntimeException rte) {
            return new ResponseEntity<>(rte.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ParseException ex) {
            return new ResponseEntity<>("FROM date or TO date is not correctly formatted", HttpStatus.BAD_REQUEST);
        } catch (DateCannotBePastException | UsernameCannotBeBlankException | ToDateBestBeBeforeFromDateException | ReservationStartsTomorrowException | StayCannotBeLongerThan3DaysException | ReservationIsTooAheadInTimeException | FromDateAndToDateCannotBeEqualException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "reserve/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> reserve(HttpServletRequest request, HttpServletResponse response) {
        try {
            String from = request.getHeader("from");
            String to = request.getHeader("to");
            String username = request.getHeader("username");
            altenWSService.validateUsername(username);
            List<Date> dateList = altenWSService.validateFromToDate(from, to);
            String code = altenWSService.doPlaceReservation(dateList.get(0), dateList.get(1));
            return new ResponseEntity<>(code, HttpStatus.OK);
        } catch (RuntimeException rte) {
            return new ResponseEntity<>(rte.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ParseException ex) {
            return new ResponseEntity<>("FROM date or TO date is not correctly formatted", HttpStatus.BAD_REQUEST);
        } catch (DateCannotBePastException | UsernameCannotBeBlankException | ToDateBestBeBeforeFromDateException | ReservationStartsTomorrowException | StayCannotBeLongerThan3DaysException | ReservationIsTooAheadInTimeException | FromDateAndToDateCannotBeEqualException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "cancel/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> cancel(HttpServletRequest request, HttpServletResponse response) {
        try {
            String code = request.getHeader("code");
            altenWSService.doCancelReservation(code);
            return new ResponseEntity<>(code, HttpStatus.OK);
        } catch (RuntimeException rte) {
            return new ResponseEntity<>(rte.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
