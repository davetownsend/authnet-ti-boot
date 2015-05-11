package io.unbounded.authnet.controller;

import io.unbounded.authnet.data.CaptureData;
import io.unbounded.authnet.data.ResponseData;
import net.authorize.Environment;
import net.authorize.Merchant;
import net.authorize.ResponseField;
import net.authorize.TransactionType;
import net.authorize.aim.Transaction;
import net.authorize.sim.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/boot/relaycapture")
public class RelayCaptureController {

  @Value("${api.login.id}")
  private String apiLoginId;

  @Value("${api.transaction.key}")
  private String transKey;


  @Value("${api.md5.hash}")
  private String mD5HashKey;

  private static final Logger log = LoggerFactory.getLogger(RelayCaptureController.class);

  @RequestMapping(value = "/relay", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseData authNetResponse(HttpServletRequest request) {
    log.info("- received post back from Authorize.net");

    Result result = Result.createResult(apiLoginId, mD5HashKey, request.getParameterMap());

    ResponseData data = new ResponseData();
    data.setTransId(result.getResponseMap().get(ResponseField.TRANSACTION_ID.getFieldName()));
    data.setCode(result.getResponseCode().name());
    data.setDescription(result.getResponseCode().getDescription());
    data.setApproved(result.isApproved());

    return data;
  }


  @RequestMapping(value = "/capture", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public CaptureData capture(@RequestBody CaptureData capture) {

    log.info("-=-=-=- performing an Authorize.net capture -=-=-=-");
    log.info("-=-=-=- transId: {}", capture.getTransId());
    log.info("-=-=-=- amount: {}", capture.getAmount());

    Merchant merch = Merchant.createMerchant(Environment.SANDBOX, apiLoginId, transKey);
    Transaction t = merch.createAIMTransaction(TransactionType.PRIOR_AUTH_CAPTURE, new BigDecimal(capture.getAmount()));
    t.setTransactionId(capture.getTransId());
    net.authorize.aim.Result<Transaction> result = (net.authorize.aim.Result<Transaction>) merch.postTransaction(t);

    log.info("ReasonText: " + result.getReasonResponseCode().getReasonText());
    log.info("ReasonReasonCode: " + result.getReasonResponseCode().getResponseCode());
    log.info("ResponseText: " + result.getResponseText());
    log.info("ResponseCode: " + result.getResponseCode().getCode());

    CaptureData data = new CaptureData();
    if (result.isApproved()) {
      log.info("-=-=-=- transaction approved!");
      data.setStatus("Approved");
    } else {
      log.info("-=-=-=- transaction NOT approved.");
      data.setStatus("Not Approved");
    }
    return data;
  }
}