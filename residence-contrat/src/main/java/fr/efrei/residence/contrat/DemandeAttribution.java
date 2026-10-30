package fr.efrei.residence.contrat;

import java.time.LocalDate;
import java.util.Set;

/** {@code refEtudiant} est la référence renvoyée par {@link SystemeResidence#inscrire}. */
public record DemandeAttribution(String refEtudiant, TypeLogement type, Set<Option> options, LocalDate dateSignature) {
}
