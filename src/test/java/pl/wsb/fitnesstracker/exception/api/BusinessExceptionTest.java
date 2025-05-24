package pl.wsb.fitnesstracker.exception.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    @Test
    void shouldCreateBusinessExceptionWithMessage() {
        // Given
        String errorMessage = "Business logic error occurred";
        
        // When
        BusinessException exception = new BusinessException(errorMessage);
        
        // Then
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void shouldHaveBadRequestResponseStatus() {
        // Given
        BusinessException exception = new BusinessException("Test error");
        
        // When
        ResponseStatus annotation = exception.getClass().getAnnotation(ResponseStatus.class);
        
        // Then
        assertNotNull(annotation);
        assertEquals(HttpStatus.BAD_REQUEST, annotation.value());
    }

    @Test
    void shouldBeRuntimeException() {
        // Given
        BusinessException exception = new BusinessException("Test error");
        
        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void shouldHandleNullMessage() {
        // When
        BusinessException exception = new BusinessException(null);
        
        // Then
        assertNull(exception.getMessage());
    }

    @Test
    void shouldPreserveStackTrace() {
        // Given
        String methodName = "shouldPreserveStackTrace";
        
        // When
        BusinessException exception = new BusinessException("Stack trace test");
        
        // Then
        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertTrue(stackTrace.length > 0);
        boolean foundThisMethod = false;
        for (StackTraceElement element : stackTrace) {
            if (element.getMethodName().equals(methodName)) {
                foundThisMethod = true;
                break;
            }
        }
        assertTrue(foundThisMethod);
    }
}