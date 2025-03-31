package com.jotalml.commonutilitylibrary.utils;

import com.jotalml.commonutilitylibrary.dto.PagedResponse2;
import com.jotalml.utils.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CommonsMethods {

    /**
     * Method to add to a list the parameters to order
     *
     * @param sort values to sort the search
     * @return Returns a list of fields to sort by
     */
    public static List<Sort.Order> getOrders(String sort, String sortDefault) {

        List<Sort.Order> orders = new ArrayList<>();

        List<String> sortList = Arrays.asList(!StringUtils.isEmpty(sort) ? sort.split(",") : sortDefault.split(","));

        sortList.forEach(
                value -> {
                    if (value.contains("-")) {
                        orders.add(new Sort.Order(Sort.Direction.DESC, value.replace("-", "")));
                    } else {
                        orders.add(new Sort.Order(Sort.Direction.ASC, value));
                    }
                });

        return orders;
    }

    /**
     * Method to compose the pagination to use in the search filter
     *
     * @param page        page number to search
     * @param sort        values to order
     * @param sortDefault values default to order
     * @return Returns the PageRequest to paginate the search results
     */
    public static PageRequest composePageable(Integer page, String sort, String sortDefault) {
        return getPageRequest(page, sort, sortDefault, Constants.MAX_RECORD_PAGE);
    }

    /**
     * Method to compose the pagination to use in the search filter
     *
     * @param page        page number to search
     * @param sort        values to order
     * @param sortDefault values default to order
     * @param pageSize    page size
     * @return Returns the PageRequest to paginate the search results
     */
    public static PageRequest composePageableWithPageSize(Integer page, String sort, String sortDefault, Integer pageSize) {
        return getPageRequest(page, sort, sortDefault, pageSize);
    }

    private static PageRequest getPageRequest(Integer page, String sort, String sortDefault, Integer pageSize) {
        PageRequest pageRequest;
        if (sort != null) {
            List<Sort.Order> orders = getOrders(sort, sortDefault);
            if (pageSize == Constants.PAGE_SIZE)
                pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(orders));
            else
                pageRequest = PageRequest.of(page, pageSize, Sort.by(orders));
        } else if (page >= 0 && pageSize > 0) {
            pageRequest = PageRequest.of(page, pageSize);
        } else if (pageSize == Constants.PAGE_SIZE) {
            pageRequest = PageRequest.of(0, Integer.MAX_VALUE);
        } else {
            pageRequest = null;
        }
        return pageRequest;
    }

    /**
     * Method to build a filter sort
     *
     * @param sortParameter sort parameter
     * @return Return a string with ordering filters
     */
    public static String buildFilterSort(String sortParameter, Map<String, String> orderingFilter) {
        List<String> sortEnd = new ArrayList<>();
        if (!Objects.isNull(sortParameter)) {
            List<String> sortInitial =
                    Arrays.stream(sortParameter.replace(" ", "").split(",")).collect(Collectors.toList());
            Map<String, String> mapOrdering = orderingFilter;
            sortInitial.forEach(
                    s -> {
                        if (mapOrdering.containsKey(s)) {
                            sortEnd.add(mapOrdering.get(s));
                        } else {
                            throw new BadRequestException(
                                    "The 'ordering' argument cannot be " + (s.isEmpty() ? "''" : s));
                        }
                    });
        }
        return !sortEnd.isEmpty()
                ? sortEnd.toString().replace("[", "").replace("]", "").replace(" ", "")
                : sortParameter;
    }

    /**
     * Generic method to set filters from controller
     *
     * @param object       FilterDto class
     * @param filterValues Map with filter values
     * @return <T>
     */
    public <T> T setFilter(Object object, Map<String, String> filterValues) {
        try {
            Class<?> clazz = object.getClass();
            T objectClass = (T) clazz.getDeclaredConstructor().newInstance();
            Method[] setterMethods = Arrays.stream(clazz.getDeclaredMethods())
                    .filter(method -> method.getName().startsWith(Constants.SET)).toArray(Method[]::new);
            Map<String, String> filter = new HashMap<>();
            //Add the filters with non-null values
            addSetterMethod(filterValues, filter);
            //Set values in FilterDto class
            invokeSetter(clazz, objectClass, filter, setterMethods);
            return objectClass;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                 ClassNotFoundException | ParseException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Add non-null values from controller to map
     *
     * @param filterValues all filter values from controller
     * @param filter       non-null filter values from controller
     */
    public void addSetterMethod(Map<String, String> filterValues, Map<String, String> filter) {
        filterValues.entrySet().stream().filter(entry -> entry.getValue() != null)
                .forEach(entry -> filter.put(Constants.SET.concat(entry.getKey().substring(0, 1).toUpperCase() + entry.getKey()
                        .substring(1)), entry.getValue()));
    }

    /**
     * Invoke setter methods through reflection
     *
     * @param clazz         runtime class of FilterDto
     * @param objectClass   create and initialize a new instance of the constructor's declaring class
     * @param filter        non-null filter values from controller
     * @param setterMethods all setter methods from FilterDto class
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws BadRequestException
     * @throws ParseException
     */
    public void invokeSetter(Class<?> clazz, Object objectClass, Map<String, String> filter, Method[] setterMethods)
            throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, ParseException {
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            for (Method m : setterMethods) {
                String methodName = m.getName();
                if (entry.getKey().equals(methodName)) {
                    String dataType = Arrays.stream(m.getParameterTypes()).findFirst().get().getName();
                    Method method = clazz.getDeclaredMethod(methodName, Class.forName(dataType));
                    method.setAccessible(true);
                    String simpleDataType = Arrays.stream(m.getParameterTypes()).findFirst().get().getSimpleName();
                    if (simpleDataType.equals("String"))
                        method.invoke(objectClass, entry.getValue().trim());
                    else if (simpleDataType.equals("Long"))
                        method.invoke(objectClass, Long.parseLong(entry.getValue()));
                    else if (simpleDataType.equals("Integer"))
                        method.invoke(objectClass, Integer.parseInt(entry.getValue()));
                    else if (simpleDataType.equals("Double"))
                        method.invoke(objectClass, Double.parseDouble(entry.getValue()));
                    else if (simpleDataType.equals("BigDecimal"))
                        method.invoke(objectClass, BigDecimal.valueOf(Double.valueOf(entry.getValue())));
                    else if (simpleDataType.equals("Boolean"))
                        method.invoke(objectClass, Boolean.valueOf(entry.getValue()));
                    else if (simpleDataType.equals("ObjectId"))
                        method.invoke(objectClass, new ObjectId(entry.getValue()));
                    else if (simpleDataType.equals("LocalDate")) {
                        String date = entry.getValue().split(",")[0];
                        String dateFormat = entry.getValue().split(",")[1];
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
                        method.invoke(objectClass, LocalDate.parse(date, formatter));
                    } else if (simpleDataType.equals("Date")) {
                        String date = entry.getValue().split(",")[0];
                        String format = entry.getValue().split(",")[1];
                        method.invoke(objectClass, new SimpleDateFormat(format).parse(date));
                    } else throw new BadRequestException("Data type not registered, only allowed " +
                            "(String, Long, Integer, Double, BigDecimal, Boolean, ObjectId and Date)");
                }
            }
        }
    }

    public void setNextAndPreviousUrl(int page, int pageSize, HttpServletRequest request, PagedResponse2<?> responseDto) {
        final String pageUrl = Constants.PAGE_URL;

        if (Objects.isNull(request))
            throw new BadRequestException(Constants.REQUEST_NULL);
        if (Objects.isNull(responseDto))
            throw new BadRequestException(Constants.PAGERESPONSE2_NULL);
        if (page < Constants.PAGE1)
            throw new BadRequestException(Constants.PAGE_RANGE);
        if (pageSize < Constants.PAGE1)
            throw new BadRequestException(Constants.PAGESIZE_RANGE);

        String requestURL = request.getRequestURL().toString();

        String fullUrl="";
        String queryString = request.getQueryString();
        if (request.getHeader(Constants.HEADER)!=null)
        {
            requestURL = requestURL.replace(request.getServletPath(),  request.getHeader(Constants.HEADER));
            fullUrl=requestURL;

        }else
        {
            fullUrl = queryString == null ? requestURL : requestURL + Constants.OPER_INTERR + queryString;
        }
        String nextUrl = fullUrl.replace((pageUrl + page), pageUrl + (page + Constants.PAGE1));

        String previousUrl = fullUrl.replace((pageUrl + page), pageUrl + (page - Constants.PAGE1));

        if (!nextUrl.contains(pageUrl))
        {
            if (request.getHeader(Constants.HEADER)!=null)
            {
                nextUrl += Constants.OPER_AMP+pageUrl + Constants.PAGE2;
            }else
            {
                if (queryString==null)
                {
                    nextUrl += Constants.OPER_INTERR + pageUrl + Constants.PAGE2;
                }else
                {
                    nextUrl += Constants.OPER_AMP+pageUrl + Constants.PAGE2;
                }
            }
        }
        if (page * pageSize > responseDto.getCount())
            nextUrl = null;

        if (page <= Constants.PAGE1)
            previousUrl = null;

        responseDto.setNext(nextUrl);
        responseDto.setPrevious(previousUrl);
    }
}