package com.cleevio.vexl.module.push.entity;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@TypeDefs({
        @TypeDef(
                name = "string-array",
                typeClass = StringArrayType.class
        )
})
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Immutable
public class Push {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String groupUuid;

    @Type(type = "string-array")
    @Column(
            name = "firebase_token",
            columnDefinition = "text[]",
            updatable = false
    )
    private String[] firebaseTokens;

    public Push(String groupUuid, String[] firebaseTokens) {
        this.groupUuid = groupUuid;
        this.firebaseTokens = firebaseTokens;
    }
}
