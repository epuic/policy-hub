package com.endava.insurance.insurance_service.application.service.document;

import com.endava.insurance.insurance_service.domain.model.Building;
import com.endava.insurance.insurance_service.domain.model.Policy;
import com.endava.insurance.insurance_service.domain.model.PolicyPremiumAdjustment;
import com.endava.insurance.insurance_service.domain.model.RiskFactor;
import com.endava.insurance.insurance_service.persistence.repository.PolicyPremiumAdjustmentRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PolicyPdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private final SpringTemplateEngine templateEngine;
    private final PolicyPremiumAdjustmentRepository premiumAdjustmentRepository;

    public byte[] generatePolicyDocument(Policy policy) {
        Context context = new Context(Locale.ENGLISH);
        Building building = policy.getBuilding();
        String currencyCode = policy.getCurrency().getCode();
        List<PolicyPremiumAdjustment> adjustments = premiumAdjustmentRepository.findByPolicyIdOrderByIdAsc(policy.getId());

        context.setVariable("policy", policy);
        context.setVariable("client", policy.getClient());
        context.setVariable("broker", policy.getBroker());
        context.setVariable("building", building);
        context.setVariable("currencyCode", currencyCode);
        context.setVariable("issueDate", formatDate(LocalDate.now()));
        context.setVariable("startDate", formatDate(policy.getStartDate()));
        context.setVariable("endDate", formatDate(policy.getEndDate()));
        context.setVariable("buildingAddress", buildingAddress(building));
        context.setVariable("buildingLocation", buildingLocation(building));
        context.setVariable("basePremium", money(policy.getBasePremiumAmount(), currencyCode));
        context.setVariable("finalPremium", money(policy.getFinalPremium(), currencyCode));
        context.setVariable("totalAdjustment", money(policy.getFinalPremium().subtract(policy.getBasePremiumAmount()), currencyCode));
        context.setVariable("insuredValue", money(BigDecimal.valueOf(building.getInsuredValue()), currencyCode));
        context.setVariable("commissionPercentage", percent(policy.getBroker().getCommissionPercentage()));
        context.setVariable("riskFactors", riskFactorLabels(building));
        context.setVariable("adjustments", adjustments);
        context.setVariable("helpers", this);

        String html = templateEngine.process("policy-document", context);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not generate policy PDF", ex);
        }
    }

    public String formatAdjustmentAmount(BigDecimal amount, String currencyCode) {
        return money(amount, currencyCode);
    }

    public String formatPercentage(BigDecimal percentage) {
        return percent(percentage);
    }

    private String buildingAddress(Building building) {
        return building.getStreet() + ", No. " + building.getNumber();
    }

    private String buildingLocation(Building building) {
        var city = building.getCity();
        var county = city.getCounty();
        var country = county.getCountry();
        return city.getName() + ", " + county.getName() + ", " + country.getName();
    }

    private List<String> riskFactorLabels(Building building) {
        return building.getRiskFactors().stream()
                .map(RiskFactor::getType)
                .map(type -> label(type.name()))
                .sorted()
                .toList();
    }

    private String formatDate(LocalDate date) {
        return date == null ? "-" : DATE_FORMATTER.format(date);
    }

    private String money(BigDecimal amount, String currencyCode) {
        if (amount == null) {
            return "-";
        }
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString() + " " + currencyCode;
    }

    private String percent(BigDecimal percentage) {
        if (percentage == null) {
            return "-";
        }
        String sign = percentage.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        return sign + percentage.stripTrailingZeros().toPlainString() + "%";
    }

    private String label(String value) {
        String[] words = value.toLowerCase(Locale.ROOT).split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!result.isEmpty()) {
                result.append(' ');
            }
            result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return result.toString();
    }
}
