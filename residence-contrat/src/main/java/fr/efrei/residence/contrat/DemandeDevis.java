package fr.efrei.residence.contrat;

import java.util.Set;

/** Loyer qu'aurait un étudiant de ce profil dans ce logement précis, avec ces options. */
public record DemandeDevis(String numeroLogement, Profil profil, int echelonBourse, Set<Option> options) {
}
