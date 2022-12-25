package com.dhmoney.transactions.domain.model.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RelatedAccountDTO {

    public String name;

    public String cvu;

    //@JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private Date lastInteractionDate;
}
