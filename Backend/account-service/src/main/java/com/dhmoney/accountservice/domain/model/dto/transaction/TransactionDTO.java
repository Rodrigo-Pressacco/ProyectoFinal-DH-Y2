package com.dhmoney.accountservice.domain.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {

    private Long id;

    private Double amount;

    //@JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private Date dated;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    private String type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long accountId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long accountIdDestination;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String origin;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String destination;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long cardID;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nameOrigin;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nameDestination;
}
