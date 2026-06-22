package com.prooftracker.notification.provider;

import com.prooftracker.common.exception.AppException;
import com.prooftracker.common.exception.ErrorCode;
import com.prooftracker.notification.enums.NotificationChannel;
import com.prooftracker.notification.provider.NotificationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationProviderFactory {

    private final List<NotificationProvider> providers;

    public NotificationProvider getProvider(
            NotificationChannel channel) {

        return providers.stream()
                .filter(provider ->
                        provider.getChannel() == channel)
                .findFirst()
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.NOTIFICATION_PROVIDER_NOT_FOUND,
                                        "Notification provider not found for channel: " + channel));
    }
}