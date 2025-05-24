package pl.wsb.fitnesstracker.exception.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    void shouldCreateNotFoundExceptionWithMessage() {
        // Given
        String errorMessage = "Resource not found";
        
        // When
        NotFoundException exception = new NotFoundException(errorMessage);
        
        // Then
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void shouldHaveNotFoundResponseStatus() {
        // Given
        NotFoundException exception = new NotFoundException("Test error");
        
        // When
        ResponseStatus annotation = exception.getClass().getAnnotation(ResponseStatus.class);
        
        // Then
        assertNotNull(annotation);
        assertEquals(HttpStatus.NOT_FOUND, annotation.value());
    }

    @Test
    void shouldExtendBusinessException() {
        // Given
        NotFoundException exception = new NotFoundException("Test error");
        
        // Then
        assertTrue(exception instanceof BusinessException);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void shouldHandleNullMessage() {
        // When
        NotFoundException exception = new NotFoundException(null);
        
        // Then
        assertNull(exception.getMessage());
    }

    @Test
    void shouldInheritBusinessExceptionBehavior() {
        // Given
        String message = "Inherited behavior test";
        NotFoundException exception = new NotFoundException(message);
        
        // When
        BusinessException businessException = exception;
        
        // Then
        assertEquals(message, businessException.getMessage());
        assertSame(exception, businessException);
    }

    @Test
    void shouldPreserveStackTrace() {
        // Given
        String methodName = "shouldPreserveStackTrace";
        
        // When
        NotFoundException exception = new NotFoundException("Stack trace test");
        
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

    @Test
    void shouldBeUsableInCatchBlocks() {
        // Given
        boolean caughtAsNotFoundException = false;
        boolean caughtAsBusinessException = false;
        boolean caughtAsRuntimeException = false;
        
        // When
        try {
            throw new NotFoundException("Test exception handling");
        } catch (NotFoundException e) {
            caughtAsNotFoundException = true;
        }
        
        try {
            throw new NotFoundException("Test exception handling");
        } catch (BusinessException e) {
            caughtAsBusinessException = true;
        }
        
        try {
            throw new NotFoundException("Test exception handling");
        } catch (RuntimeException e) {
            caughtAsRuntimeException = true;
        }
        
        // Then
        assertTrue(caughtAsNotFoundException);
        assertTrue(caughtAsBusinessException);
        assertTrue(caughtAsRuntimeException);
    }
}