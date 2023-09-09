package ca.ulaval.glo4003.ws.domain.contact;


import ca.ulaval.glo4003.ws.api.contact.dto.ContactDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ContactService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ContactService.class);

  private ContactRepository contactRepository;
  private ContactAssembler contactAssembler;

  public ContactService(ContactRepository contactRepository, ContactAssembler contactAssembler) {
    this.contactRepository = contactRepository;
    this.contactAssembler = contactAssembler;
  }

  public List<ContactDto> findAllContacts() {
    LOGGER.info("Get all contacts");
    List<Contact> contacts = contactRepository.findAll();
    return contacts.stream().map(contactAssembler::create).collect(Collectors.toList());
  }

  public ContactDto findContact(String id) {
    LOGGER.info("Get contact with id {}", id);
    Contact contact = contactRepository.findById(id);
    return contactAssembler.create(contact);
  }

  public void addContact(ContactDto contactDto) {
    LOGGER.info("Add new contact {}", contactDto);
    Contact contact = contactAssembler.create(contactDto);
    contactRepository.save(contact);
  }

  public void updateContact(String id, ContactDto contactDto)
    throws ContactNotFoundException {
    LOGGER.info("Update contact with id {}", id);
    Contact contact = contactAssembler.create(id, contactDto);
    contactRepository.update(contact);
  }

  public void deleteContact(String id) {
    LOGGER.info("Delete contact with id {}", id);
    contactRepository.remove(id);
  }
}
