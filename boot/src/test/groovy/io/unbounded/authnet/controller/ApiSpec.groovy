package io.unbounded.authnet.controller

import groovy.json.JsonBuilder
import groovyx.net.http.RESTClient
import io.unbounded.authnet.Application
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = Application.class)
@WebAppConfiguration
@IntegrationTest('spring.profiles.active=test')
class ApiSpec extends Specification {

  @Value('${api.uri}')
  def uri

  def endpoint

  def setup() {
    endpoint = new RESTClient(uri)
  }


  def "should return a 200 and Content-Type application/json when GETing /access"() {
    given:
    def path = '/boot/authnet/access'
    def query = [amount: 1200]

    when:
    def response = endpoint.get([path: path, query: query])

    then:
    with(response) {
      status == 200
      contentType == 'application/json'
    }
  }


  def "should return a 200 and Content-Type application/json when POSTing to /relay"() {
    given:
    def path = '/boot/relaycapture/relay'

    when:
    def response = endpoint.post([path: path])

    then:
    with(response) {
      status == 200
      contentType == 'application/json'
    }
  }


  def "should return a 200 and Content-Type application/json when POSTing to /capture"() {
    given:
    def path = '/boot/relaycapture/capture'
    def payload = new JsonBuilder()
    payload {
      transId '1111'
      amount '1500'
    }

    when:
    def response = endpoint.post(
            [path: path, body: payload.toString(), requestContentType: 'application/json']
    )

    then:
    with(response) {
      status == 200
      contentType == 'application/json'
      data.status == 'Not Approved'
    }
  }
}