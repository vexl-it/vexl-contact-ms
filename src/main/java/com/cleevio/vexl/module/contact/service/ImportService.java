package com.cleevio.vexl.module.contact.service;

import com.cleevio.vexl.module.contact.dto.request.ImportRequest;
import com.cleevio.vexl.module.contact.dto.response.ImportResponse;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.contact.entity.UserContact;
import com.cleevio.vexl.module.contact.exception.ContactsMissingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for importing contacts. All contacts are from phoneHash/facebookIdHash and contact encrypted with HmacSHA256.
 * We get contacts not encrypted, so we need to encrypt them on BE.
 *
 */
@Service
@Slf4j
@AllArgsConstructor
public class ImportService {

    private final ContactRepository contactRepository;
    private final ContactService contactService;

    @Transactional(rollbackFor = Exception.class)
    public ImportResponse importContacts(User user, ImportRequest importRequest)
            throws ContactsMissingException {

        int importSize = importRequest.getContacts().size();
        List<byte[]> contacts = new ArrayList<>();

        log.info("Importing new {} contacts for {}",
                importRequest.getContacts().size(),
                user.getId());

        if (importRequest.getContacts().isEmpty()) {
            throw new ContactsMissingException();
        }

        for (String contact : importRequest.getContacts()) {
            contacts.add(this.contactService.calculateHmacSha256(contact.trim()));
        }

        AtomicInteger imported = new AtomicInteger();

        contacts
                .forEach(p -> {
                    if (!this.contactRepository.existsByHashFromAndHashTo(user.getHash(), p)) {
                        UserContact contact = UserContact.builder()
                                .hashFrom(user.getHash())
                                .hashTo(p)
                                .build();
                        this.contactRepository.save(contact);
                        imported.getAndIncrement();
                    }
                });

        String message = String.format("Imported %s / %s contacts.",
                imported.get(),
                importSize);

        log.info(message);

        return new ImportResponse(true, message);
    }
}
