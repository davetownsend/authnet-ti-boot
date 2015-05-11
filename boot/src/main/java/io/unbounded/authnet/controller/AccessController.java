package io.unbounded.authnet.controller;

import io.unbounded.authnet.data.FingerprintData;
import net.authorize.sim.Fingerprint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/boot/authnet")
public class AccessController {

  @Value("${api.login.id}")
  private String apiLoginId;

  @Value("${api.transaction.key}")
  private String transKey;


  @RequestMapping(value = "/access", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public FingerprintData authAccess(@RequestParam(value = "amount", required = true) String amount) {
    Fingerprint fp = getFingerPrint(amount);
    return new FingerprintData(
            fp.getSequence(),
            fp.getTimeStamp(),
            fp.getFingerprintHash()
    );
  }


  private Fingerprint getFingerPrint(String amount) {
    long seq = (long) Math.floor(Math.random() * 9000000000L) + 1000000000L;
    return Fingerprint.createFingerprint(
            apiLoginId,
            transKey,
            seq,
            amount
    );
  }
}