package pl.wsb.fitnesstracker.training.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.wsb.fitnesstracker.mail.api.EmailDto;
import pl.wsb.fitnesstracker.mail.api.EmailSender;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.user.internal.UserRepository;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MonthlyTrainingSummaryScheduler {

    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final EmailSender emailSender;

    @Scheduled(cron = "0 20 21 * * *", zone = "Europe/Warsaw")
    public void sendMonthlySummary() {
        ZoneId zone = ZoneId.systemDefault();

        LocalDate firstDayOfPrevMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfPrevMonth = firstDayOfPrevMonth.withDayOfMonth(firstDayOfPrevMonth.lengthOfMonth());

        LocalDateTime startOfMonth = firstDayOfPrevMonth.atStartOfDay();
        LocalDateTime endOfMonth = lastDayOfPrevMonth.atTime(23, 59, 59);

        Date startDate = Date.from(startOfMonth.atZone(zone).toInstant());
        Date endDate = Date.from(endOfMonth.atZone(zone).toInstant());

        userRepository.findAll().forEach(user -> {
            List<Training> trainings = trainingRepository.findByUserAndStartTimeBetween(user, startDate, endDate);

            String htmlContent = buildHtmlEmail(user.getFirstName(), trainings);

            EmailDto email = new EmailDto(
                    user.getEmail(),
                    "Miesięczne podsumowanie treningów",
                    htmlContent,
                    true
            );

            emailSender.send(email);

        });
    }

    private String buildHtmlEmail(String firstName, List<Training> trainings) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html><body>");
        sb.append("<p>Cześć ").append(firstName).append(",</p>");
        sb.append("<p>W poprzednim miesiącu zarejestrowałeś(-aś) ")
                .append(trainings.size()).append(" treningów.</p>");

        sb.append("<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>");
        sb.append("<thead><tr>")
                .append("<th>Początek</th>")
                .append("<th>Koniec</th>")
                .append("<th>Typ aktywności</th>")
                .append("<th>Dystans (km)</th>")
                .append("<th>Śr. prędkość (km/h)</th>")
                .append("</tr></thead><tbody>");

        for (Training training : trainings) {
            sb.append("<tr>")
                    .append("<td>").append(formatDate(training.getStartTime())).append("</td>")
                    .append("<td>").append(formatDate(training.getEndTime())).append("</td>")
                    .append("<td>").append(training.getActivityType()).append("</td>")
                    .append("<td>").append(training.getDistance()).append("</td>")
                    .append("<td>").append(training.getAverageSpeed()).append("</td>")
                    .append("</tr>");
        }

        sb.append("</tbody></table>");
        sb.append("<p>Ćwicz dalej!</p>");
        sb.append("</body></html>");

        return sb.toString();
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

}
