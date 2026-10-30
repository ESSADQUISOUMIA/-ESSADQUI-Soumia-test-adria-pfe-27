package ma.adria.transactionservice.web.rest;
import org.springframework.web.bind.annotation.*;import org.springframework.http.*;
@RestControllerAdvice public class Errors {@ExceptionHandler({IllegalArgumentException.class,ArithmeticException.class,NullPointerException.class}) public ProblemDetail invalid(Exception ex){return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Requête invalide : "+ex.getMessage());}}
