package gdse71.project.animalhospital.dto.PetTm;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class PaymentInvoiceTM {
    private String paymentId;
    private String paymentDate;
    private String paymentMethod;
    private String InvoiceNo;
    private String InvoiceName;
    private Double InvoiceAmount;
  //  private String payID;
}
