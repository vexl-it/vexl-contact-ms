package com.cleevio.vexl.module.temp;

import com.cleevio.vexl.common.enums.AlgorithmEnum;
import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.contact.dto.request.ImportRequest;
import com.cleevio.vexl.module.contact.exception.ContactsMissingException;
import com.cleevio.vexl.module.contact.service.ContactService;
import com.cleevio.vexl.module.contact.service.ImportService;
import com.cleevio.vexl.module.user.entity.User;
import com.cleevio.vexl.module.user.service.UserService;
import com.cleevio.vexl.utils.EncryptionUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Tag(name = "Temp")
@RestController
@RequestMapping(value = "temp")
@AllArgsConstructor
public class TempController {

    private final UserService userService;
    private final ContactService contactService;
    private final ImportService importService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> createContacts(@RequestHeader(name = SecurityFilter.HEADER_PUBLIC_KEY) String publicKey,
                                        @RequestHeader(name = SecurityFilter.HEADER_HASH) String hash) throws ContactsMissingException, NoSuchAlgorithmException {
        Optional<User> user = this.userService.findByPublicKeyAndHash(publicKey, hash);
        ImportRequest importRequest = new ImportRequest();
        importRequest.setContacts(generateContacts());
        this.importService.importContacts(user.get(), importRequest);
        return ResponseEntity.noContent().build();
    }

    private List<String> generateContacts() throws NoSuchAlgorithmException {
        List<String> contacts = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            KeyPair keyPair = EncryptionUtils.retrieveKeyPair(AlgorithmEnum.ECIES.getValue());
            Random random = new Random();
            int nextInt = random.nextInt(10000);
            String contact = String.valueOf(nextInt);
            byte[] hash = this.contactService.calculateHmacSha256(contact);
            this.userService.createUser(
                    EncryptionUtils.encodeToBase64String(keyPair.getPublic().getEncoded()),
                    EncryptionUtils.encodeToBase64String(hash));
            contacts.add(contact);
        }
        return contacts;

    }
}
