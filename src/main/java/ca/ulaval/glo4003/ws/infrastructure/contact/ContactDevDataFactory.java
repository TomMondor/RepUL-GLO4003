package ca.ulaval.glo4003.ws.infrastructure.contact;

import ca.ulaval.glo4003.ws.domain.contact.Contact;

import java.util.List;

public class ContactDevDataFactory {

  public List<Contact> createMockData() {
    Contact jobs = new Contact("1", "514-999-0000", "California", "Steve Jobs");
    Contact balmer = new Contact("2", "781-888-1111", "Manitoba", "Steve Balmer");
    Contact franklin = new Contact("3", "964-543-6475", "Washington", "Benjamin Franklin");

    return List.of(jobs, balmer, franklin);
  }
}
