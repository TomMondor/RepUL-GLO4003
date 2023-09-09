package ca.ulaval.glo4003.ws.api.contact.dto;

public record ContactDto(
  String id,
  String telephoneNumber,
  String address,
  String name
) {}
