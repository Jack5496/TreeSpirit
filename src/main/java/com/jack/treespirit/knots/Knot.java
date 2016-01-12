package com.jack.treespirit.knots;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.jack.treespirit.Core;
import com.jack.treespirit.functions.LocationSerialize;
import com.jack.treespirit.signs.Signs;
import com.jack.treespirit.threads.DamageController;

public class Knot implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -344478399844068277L;
	/**
	 * Keine TREEMAP nehmen: in a HashMap, moving from 1000 items to 10,000
	 * doesn't really affect your time to lookup an element, but for a TreeMap
	 * the lookup time will be about 3 times slower (assuming Log2). Moving from
	 * 1000 to 100,000 will be about 6 times slower for every element lookup. *
	 */

	public String s_loc; // Location des Knoten
	public Integer i_kosten; // Günstigste kosten !Insgesammt
	public String s_kosten; // Günstigster String / Location
	public HashMap<String, List<String>> inhalt; // Totale Kosten, Weg
													// Information
	private boolean root;
	public boolean onUpdate;
	public boolean createSignOnKnot = Core.configs.getCreate_Sign_On_Knot();

	public Knot(Location l, boolean root) {
		if (root) {
			s_loc = LocationSerialize.serializeFromLocation(l);
			this.root = true;
			this.i_kosten = new Integer(0);
			this.s_kosten = this.s_loc;
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Kosten von Root: " + i_kosten);
			}
			inhalt = new HashMap<String, List<String>>();
			onUpdate = false;
		} else {
			this.createKnot(l);
		}
	}

	public Knot(Location l) {
		this.createKnot(l);
	}

	private void createKnot(Location l) {
		if (l.getBlock().getType() != Material.LEAVES
				&& l.getBlock().getType() != Material.LEAVES_2) {
			s_loc = LocationSerialize.serializeFromLocation(l);
			root = false;
			inhalt = new HashMap<String, List<String>>();
			onUpdate = false;

			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Erstelle Knoten an Loc: " + s_loc);
			}

			if (createSignOnKnot) {
				Block b = l.getWorld().getBlockAt(
						new Location(l.getWorld(), l.getBlockX(),
								l.getBlockY() + 1, l.getBlockZ()));
				if (b.getType() == Material.AIR) {
					b.setType(Material.SIGN_POST);
					Sign signState = (Sign) b.getState();
					String top = "[To Root]";
					signState.setLine(0, top);
					signState.update();
				}
			}

			updateAllWays();
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Alle wege gefunden: " + inhalt.size());
			}
			updateCost();
			updateCostAtNeighbors();
		} else {
			Core.getInstance().removeFromKnotMap(l);
		}
	}

	public void removeLocationWarningSpecialUseOnly(Location l) {
		String s_dir = LocationSerialize.serializeFromLocation(l);
		inhalt.remove(s_dir); // Entferne richtung aus Hashmap
	}

	/**
	 * Entfern sicher einen Eintrag aus dem Knoten
	 * 
	 * @param l
	 *            Location die entfernt werden soll
	 */
	public void removeLocationFromList(Location l) {
		String s_dir = LocationSerialize.serializeFromLocation(l);
		removeLocationWarningSpecialUseOnly(l);
		if (inhalt.size() == 0) {
			i_kosten = null;
			s_kosten = null;
			Signs.updateSignCost(this);
		} else if (s_dir.equalsIgnoreCase(s_kosten)) { // Falls kürzester weg
														// gelöscht wurde
			if (i_kosten != null) {
				String otherWay = isThereAnotherWayWithSameCost(i_kosten);
				if (otherWay != null) {
					s_kosten = otherWay;
					Signs.updateSignCost(this);
					return;
				}
			}

			s_kosten = null;
			i_kosten = null;

			final Knot k = this;

			Core.getInstance()
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(Core.getInstance(),
							new Runnable() {

								public void run() {

									Signs.updateSignCost(k);

									onUpdate = true;

									List<Knot> knots = setAllKnotsUsingMeOnUpdate();

									if (Core.getInstance().getConfigurations()
											.getDebug_Mode()) {
										Bukkit.broadcastMessage("Das war mein bester weg");
										Bukkit.broadcastMessage("Starte nearby aktion");
									}

									updateCostAtNeighbors();

									onUpdate = false;

									setAllKnotsOnUpdate(knots, false);
									updateCost();

									// NEu Es könnte sein, dass ich nur einen
									// besseren weg nahm
									// und meine nachbarn daraufhin nicht
									// geupdatet werden konnten
									updateCostAtNeighbors();

								}
							}, 0L);
		}
	}

	public String isThereAnotherWayWithSameCost(Integer i) {
		List<String> sortedKeys = new ArrayList<String>(inhalt.keySet());
		for (String way : sortedKeys) {
			String knot_loc = getInhalt(way, 0);
			if (knot_loc != null) {
				Knot k = Core.getInstance().getKnot(
						LocationSerialize.serializeToLocation(knot_loc));
				if (k != null) {
					if (!k.onUpdate) {
						Integer knot_cost = k.i_kosten;
						Integer way_cost = Integer.parseInt(getInhalt(way, 1));
						if (knot_cost != null && way_cost != null) {
							Integer total = knot_cost + way_cost;
							if (total.intValue() == i_kosten.intValue()) {
								return way;
							}
						}
					}
				}
			}
		}
		return null;
	}

	public List<Knot> setAllKnotsUsingMeOnUpdate() {
		List<String> sortedKeys = new ArrayList<String>(inhalt.keySet());
		List<Knot> back = new ArrayList<Knot>();
		for (String way : sortedKeys) {
			String knot_string = getInhalt(way, 0);
			if (knot_string != null) {
				Location knot_loc = LocationSerialize
						.serializeToLocation(knot_string);
				if (Core.getInstance().isInKnotMap(knot_loc)) {
					Knot k = Core.getInstance().getKnot(knot_loc);
					if (k != null) {
						if (k.s_kosten != null) {
							String bestKnotString = k.getInhalt(k.s_kosten, 0);
							if (bestKnotString != null) {
								if (bestKnotString.equalsIgnoreCase(s_loc)) {
									if (k.onUpdate == false) {
										back.add(k);
										k.onUpdate = true;

										List<Knot> extra = k
												.setAllKnotsUsingMeOnUpdate();

										for (Knot k2 : extra) {
											back.add(k2);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return back;
	}

	public void setAllKnotsOnUpdate(List<Knot> knots, boolean b) {
		for (Knot k : knots) {
			if (k != null) {
				k.onUpdate = b;
			}
		}
	}

	public boolean NotImpossibleWay(String way) {
		if (s_loc != null) {
			Location myloc = LocationSerialize.serializeToLocation(s_loc);
			Location way_loc = LocationSerialize.serializeToLocation(way);
			if (myloc.distance(way_loc) > 1.8) {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("There's an Illegal way");
				}
				return false;
			} else {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Der weg von : " + s_loc + " nach "
							+ way_loc + " is ok , da: "
							+ (myloc.distance(way_loc)));
				}
				return true;
			}
		} else {
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("s_loc ist null");
			}
			return true;
		}
	}

	public void addLocationToList(String way, String knot_loc, Integer cost) {
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage("Füge Weg hinzu");
		}
		if (NotImpossibleWay(way)) {
			if (LocationSerialize.serializeToLocation(way).getBlock().getType() != Material.LEAVES) {
				List<String> value = new ArrayList<String>();
				value.add(knot_loc);
				value.add("" + cost);
				inhalt.put(way, value);
			}
		}
	}

	public void updateMeOnce() {

		UUID uuid = Core.getInstance().getUUIDFromHashMap(
				LocationSerialize.serializeToLocation(s_loc));
		Location myloc = LocationSerialize.serializeToLocation(s_loc);

		List<String> sortedKeys = new ArrayList<String>(inhalt.keySet());

		Core.getInstance().removeFromHashMap(myloc); // Problem beim suchen des
														// nächsten Knoten, wird
														// am ende wieder
														// hinzugefügt

		List<Knot> k_list = new ArrayList<Knot>();
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage("Checke umliegende Blöcke");
		}
		for (String s : sortedKeys) {
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Update Me (" + s_loc + ") at : "
						+ getInhalt(s, 0));
			}

			Location s_l = LocationSerialize.serializeToLocation(s);

			Location loc = LocationSerialize
					.serializeToLocation(getInhalt(s, 0));

			if (s_l.equals(loc)) {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Knoten direkt neben mir");
				}
				Knot k2 = Core.getInstance().getKnot(loc);
				k_list.add(k2);
				k2.addLocationToList(s_loc, s_loc, new Integer(1));
			} else {

				if (Core.getInstance().isInKnotMap(loc)) {
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage("Suche nächsten Knoten in richtung: "
								+ s);
					}
					Object[] obj = Knot.findNextKnot(s_l, uuid);
					if (obj != null) {
						if (Core.getInstance().getConfigurations()
								.getDebug_Mode()) {
							Bukkit.broadcastMessage("gefunden");
						}
						Knot k2 = Core.getInstance().getKnot((Location) obj[0]);

						if (Core.getInstance().getConfigurations()
								.getDebug_Mode()) {
							Bukkit.broadcastMessage("Update me once");
						}
						k2.removeLocationFromList((Location) obj[2]);
						k_list.add(k2);

						String way_from_knot = LocationSerialize
								.serializeFromLocation((Location) obj[2]);

						if (Core.getInstance().getConfigurations()
								.getDebug_Mode()) {
							Bukkit.broadcastMessage("Knot: " + k2.s_loc
									+ " add Connection: " + way_from_knot);
						}
						k2.addLocationToList(way_from_knot, s_loc,
								Integer.parseInt(getInhalt(s, 1)));
					} else {
						if (Core.getInstance().getConfigurations()
								.getDebug_Mode()) {
							Bukkit.broadcastMessage("Nichts gefunden, darf nicht sein");
						}
					}
				} else {
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage("Darf nicht passieren");
					}
				}

			}
		}

		Core.getInstance().addToHashMap(myloc, uuid);

		for (Knot k2 : k_list) {
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Knoten :" + k2.s_loc
						+ " mach cost update");
			}
			k2.updateCost();
		}

	}

	public void updateCostAtNeighbors() {
		List<String> sortedKeys = new ArrayList<String>(inhalt.keySet());
		for (String way : sortedKeys) {
			String s = getInhalt(way, 0);
			if (s != null) {
				Knot k = Core.getInstance().getKnot(
						LocationSerialize.serializeToLocation(s));

				if (k != null) {
					k.updateCost();
				} else {
					inhalt.remove(way);
				}
			} else {
				inhalt.remove(way);
			}
		}
	}

	public void updateCost() {
		if (this!=null) {
			if (root) {
				return;
			}
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Update Cost wird gestartet");
			}

			List<String> sortedKeys = new ArrayList<String>(inhalt.keySet());
			Integer old_best = null;
			if (i_kosten != null) {
				old_best = new Integer(i_kosten.intValue());
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Merke allte Größe war: "
							+ old_best);
				}
			} else {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Alte Größe nicht vorhanden");
				}
			}
			i_kosten = null;

			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Suche billigsten weg von: "
						+ sortedKeys.size() + " aus");
			}
			for (String way : sortedKeys) {

				String s_location = getInhalt(way, 0);

				Knot k = Core.getInstance().getKnot(
						LocationSerialize.serializeToLocation(s_location));
				String way_cost_string = (getInhalt(way, 1));

				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Prüfe kosten von: " + s_location
							+ " Knoten == null ? : " + (k == null));
				}
				if (k != null) {
					if (Core.getInstance().isInHashMap(
							LocationSerialize.serializeToLocation(s_location))) {

						Location loc_of_knot = LocationSerialize
								.serializeToLocation(s_location);
						String other_knot_rep = Core.getInstance()
								.getRepresentant(
										Core.getInstance().getUUIDFromHashMap(
												loc_of_knot));

						Location my_loc = LocationSerialize
								.serializeToLocation(s_loc);
						String my_rep = Core.getInstance().getRepresentant(
								Core.getInstance().getUUIDFromHashMap(my_loc));

						/**
						 * Abfrage ob es sich um KnotenPunkte des selben Baumes
						 * handelt, jeder Knoten kennt alle angrenzenden Knoten,
						 * um beim Mergen direkt dann darüber eine Verbindung zu
						 * finden.
						 */
						if (my_rep == null)
							return;
						if (other_knot_rep == null)
							return;

						if (my_rep.equalsIgnoreCase(other_knot_rep)) {

							if (!k.onUpdate) {

								if (Core.getInstance().getConfigurations()
										.getDebug_Mode()) {
									Bukkit.broadcastMessage("Knoten ("
											+ k.s_loc + ") mit kosten zu ihm: "
											+ way_cost_string);
								}
								if (k.i_kosten == null) {
									if (Core.getInstance().getConfigurations()
											.getDebug_Mode()) {
										Bukkit.broadcastMessage("Knoten kostet unendlich");
									}
									if (k.isRoot()) {
										if (Core.getInstance()
												.getConfigurations()
												.getDebug_Mode()) {
											Bukkit.broadcastMessage("Weg in richtung: "
													+ way + " führt zur Root");
											Bukkit.broadcastMessage("Waycost_string ist: "
													+ way_cost_string);
										}
										int way_cost = Integer
												.parseInt(way_cost_string);
										if (i_kosten == null) {
											i_kosten = new Integer(way_cost);
											s_kosten = way;
											Signs.updateSignCost(this);
										} else if (i_kosten > way_cost) { // Falls
																			// günstigere
																			// Weg
											// gefunden
											i_kosten = new Integer(way_cost);
											s_kosten = way;
											Signs.updateSignCost(this);
										}
										if (Core.getInstance()
												.getConfigurations()
												.getDebug_Mode()) {
											Bukkit.broadcastMessage("new i_kosten sind: "
													+ i_kosten);
										}
									}
								} else {
									if (Core.getInstance().getConfigurations()
											.getDebug_Mode()) {
										Bukkit.broadcastMessage("Kosten des Knoten: "
												+ k.i_kosten);
									}
								}
								if (!((k.i_kosten == null) || (way_cost_string == null))) {

									String knot_of_best_way_from_knot = k
											.getInhalt(k.s_kosten, 0);
									if (Core.getInstance().getConfigurations()
											.getDebug_Mode()) {
										Bukkit.broadcastMessage("Bester Knoten des zu prüfenden Knoten ist: ");
									}
									if (knot_of_best_way_from_knot == null) {
										if (Core.getInstance()
												.getConfigurations()
												.getDebug_Mode()) {
											Bukkit.broadcastMessage("Nicht vorhanden");
										}
										int knot_cost = k.i_kosten;
										int way_cost = Integer
												.parseInt(way_cost_string);
										int total = way_cost + knot_cost;
										if (total >= 0) {
											if (i_kosten == null) { // Falls
																	// noch
																	// nicht
																	// gesetzt
												i_kosten = new Integer(total);
												s_kosten = way;
												Signs.updateSignCost(this);
											} else if (i_kosten > total) { // Falls
																			// günstigere
																			// Weg
																			// gefunden
												i_kosten = new Integer(total);
												s_kosten = way;
												Signs.updateSignCost(this);
											}
										}
									} else if (!knot_of_best_way_from_knot
											.equals(s_loc)) { // alle
																// deren
																// bester
																// weg
																// ich
																// nicht
																// bin
										if (Core.getInstance()
												.getConfigurations()
												.getDebug_Mode()) {
											Bukkit.broadcastMessage("jedenfalls nicht ich");
										}
										int knot_cost = k.i_kosten;
										int way_cost = Integer
												.parseInt(way_cost_string);
										int total = way_cost + knot_cost;
										if (Core.getInstance()
												.getConfigurations()
												.getDebug_Mode()) {
											Bukkit.broadcastMessage("Prüfe was dieser weg kosten würde");
										}
										if (total >= 0) {
											if (i_kosten == null) { // Falls
																	// noch
																	// nicht
																	// gesetzt
												i_kosten = new Integer(total);
												s_kosten = way;
												Signs.updateSignCost(this);
												if (Core.getInstance()
														.getConfigurations()
														.getDebug_Mode()) {
													Bukkit.broadcastMessage("Irgendwas, aber ich koste unendlich, daher nehme ich an");
												}
											} else if (i_kosten > total) { // Falls
																			// günstigere
																			// Weg
																			// gefunden
												i_kosten = new Integer(total);
												s_kosten = way;
												Signs.updateSignCost(this);
												if (Core.getInstance()
														.getConfigurations()
														.getDebug_Mode()) {
													Bukkit.broadcastMessage("Ist günstiger als mein bester");
												}
											} else {
												if (Core.getInstance()
														.getConfigurations()
														.getDebug_Mode()) {
													Bukkit.broadcastMessage("Ist teurer als mein bester");
												}
											}
										} else {
											if (Core.getInstance()
													.getConfigurations()
													.getDebug_Mode()) {
												Bukkit.broadcastMessage("Kostet negativ");
											}
										}
									} else {
										if (Core.getInstance()
												.getConfigurations()
												.getDebug_Mode()) {
											Bukkit.broadcastMessage("Bin ich");
										}
									}
								}

							}

						}

					} else {
						Bukkit.broadcastMessage("Haha dieser Knoten ist nicht existent");
						// removeLocationFromList(LocationSerialize
						// .serializeToLocation(way));
						this.removeLocationWarningSpecialUseOnly(LocationSerialize
								.serializeToLocation(way));
					}
				} else {
				}
			}

			// REKURSIONS ANKER

			// sofern sich was geändert hat bescheid sagen

			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Fertig mit suche nach besten Weg");
			}

			if (old_best == null) {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Ich kostete vorher unendlich");
				}
				if (i_kosten == null) {
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage("Ich bleibe unendlich");
					}


					return;
				} else {
					onUpdate = false;
					Signs.updateSignCost(this);
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage("update Cost A");
					}
					updateCostAtNeighbors();
					return;
				}
			}
			if (i_kosten == null) {

				onUpdate = false;

				Signs.updateSignCost(this);
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("update Cost B");
				}
				updateCostAtNeighbors();

				return;
			}
			if (!i_kosten.equals(old_best)) {

				onUpdate = false;

				Signs.updateSignCost(this);
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("update Cost  C");
				}
				this.updateNeighbors();
				updateCostAtNeighbors();
			} else {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Alles beim alten geblieben");
				}
			}
		}
	}

	/**
	 * Gibt den inhalt der Hashmap wieder
	 * 
	 * @param a
	 * @param i
	 *            i==0 -> KnotenLoc || i==1 -> Kosten dorthin
	 * @return
	 */
	public String getInhalt(String a, int i) {
		if (inhalt.containsKey(a)) {
			if (i == 0 || i == 1) {
				return inhalt.get(a).get(i);
			}
			return null;
		}
		return null;
	}

	public boolean isRoot() {
		return this.root;
	}

	public boolean isConnectedToRoot() {
		if (this.root) {
			return true;
		} else {
			if (i_kosten == null) {
				return false;
			}
			if (s_kosten == null) {
				return false;
			}
			return !inhalt.isEmpty();
		}
	}

	public void resetInhalt() {
		inhalt = new HashMap<String, List<String>>();
	}

	public void updateAllWays() {
		if (root) {
			return;
		}
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage("Update all Ways an: " + s_loc);
		}
		this.s_kosten = null;
		Location l = LocationSerialize.serializeToLocation(s_loc);
		List<Location> neighbors = getNeighbors(l, Core.getInstance()
				.getUUIDFromHashMap(l));
		neighbors.remove(l);

		List<String> besucht = new ArrayList<String>();
		for (Location loc : neighbors) {
			besucht.add(LocationSerialize.serializeFromLocation(loc));
		}
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage("Besuche Blöcke: " + besucht.size());
		}

		List<String> durchgehen = new ArrayList<String>(besucht);

		for (String d : durchgehen) {
			besucht.remove(d);
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Gehe block weiter");
			}
			Integer steps = new Integer(0);
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Suche nächsten Knoten in Richtung: "
						+ d);
			}

			Object[] info = findNextKnot(l, besucht, steps, Core.getInstance()
					.getUUIDFromHashMap(l));

			if (info != null) {
				Location loc_knot = (Location) info[0];
				String loc_knot_string = LocationSerialize
						.serializeFromLocation(loc_knot);
				steps = new Integer(((Integer) info[1]).intValue());

				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Knoten gefunden ("
							+ loc_knot_string + ") mit Schritten: " + steps);
				}
				addLocationToList(d, loc_knot_string, steps);

			} else {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Keinen gefunden");
				}
			}

			besucht.add(d);

		}

	}

	public void updateNeighbors() {
		Location l = LocationSerialize.serializeToLocation(s_loc);
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage("Location von Start Knoten: " + s_loc);
		}
		List<Location> neighbors = getNeighbors(l, Core.getInstance()
				.getUUIDFromHashMap(l));

		List<String> besucht = new ArrayList<String>();
		for (Location loc : neighbors) {
			besucht.add(LocationSerialize.serializeFromLocation(loc));
		}
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage("Besuche Blöcke: " + besucht.size());
		}

		List<String> durchgehen = new ArrayList<String>(besucht);

		for (String d : durchgehen) {
			besucht.remove(d);
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Gehe block weiter");
			}
			Integer steps = new Integer(0);
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Suche in Richtung: " + d);
			}
			Object[] info = findNextKnot(l, besucht, steps, Core.getInstance()
					.getUUIDFromHashMap(l));

			if (info != null) {
				Location knot_loc = (Location) info[0];
				Knot k = Core.getInstance().getKnot(knot_loc);

				Location way = (Location) info[2];
				String way_string = LocationSerialize
						.serializeFromLocation(way);
				steps = (Integer) info[1];
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Knoten gefunden (" + knot_loc
							+ ") mit Schritten: " + steps);
				}

				k.addLocationToList(way_string, s_loc, steps);
			}

			besucht.add(d);
		}

	}

	public static void checkForConnectionBetweenKnots(Location l, UUID uuid) {
		List<Location> neigh = Knot.getNeighbors(l, uuid);
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage("Checke für Block: " + l.toString());
		}

		if (neigh.size() == 2) {

			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Check connection between Knots");
			}

			Core.getInstance().removeFromHashMap(l); // Problem beim suchen des
														// nächsten Knoten, wird
														// am ende wieder
														// hinzugefügt

			List<Knot> k_list = new ArrayList<Knot>();

			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Checke umliegende Blöcke");
			}
			for (Location s : neigh) {
				if (!Core.getInstance().isInKnotMap(s)) {
					Object[] obj = Knot.findNextKnot(s, Core.getInstance()
							.getUUIDFromHashMap(s));
					if (obj != null) {
						Knot k = Core.getInstance().getKnot((Location) obj[0]);
						if (Core.getInstance().getConfigurations()
								.getDebug_Mode()) {
							Bukkit.broadcastMessage("Remove in Connection check");
						}
						k.removeLocationWarningSpecialUseOnly((Location) obj[2]);
						// k.removeLocationFromList((Location) obj[2]);
						k_list.add(k);
					}
				} else {
					Knot k = Core.getInstance().getKnot(s);
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage("Remove in Connection check 2");
					}
					k.removeLocationWarningSpecialUseOnly(l);
					// k.removeLocationFromList(l);
					k_list.add(k);
				}
			}

			Core.getInstance().addToHashMap(l, uuid);

			for (Knot k : k_list) {
				k.updateAllWays();
				k.updateCost();
			}

		}
	}

	public Location getLocation() {
		return LocationSerialize.serializeToLocation(s_loc);
	}

	/**
	 * Sucht den nächten Knotenpunkt falls vorhanden
	 * 
	 * @param l
	 *            Location von der gesucht werden soll
	 * @param UUID
	 *            uuid, die uuid für welche Gruppe
	 * @return Object[] Object[0] = Location des Knotenpunktes Object[1] =
	 *         steps; Object[2] = Location der Richtung an dem Knoten; null bei
	 *         keiner Verbindung
	 */
	public static Object[] findNextKnot(Location l, UUID uuid) {
		List<String> besucht = new ArrayList<String>();
		List<String> noch_zu_prüfen = new ArrayList<String>();
		return findNextKnot(l, besucht, noch_zu_prüfen, new Integer(0), uuid);
	}

	/**
	 * Sucht den nächten Knotenpunkt falls vorhanden
	 * 
	 * @param l
	 *            Location von der gesucht werden soll
	 * @param List
	 *            <String> besucht, besuchte Locations
	 * @param Integer
	 *            steps Anzahl benötigter schritte
	 * @param UUID
	 *            uuid, die uuid für welche Gruppe
	 * @return Object[] Object[0] = Location des Knotenpunktes Object[1] =
	 *         steps; Object[2] = Location der Richtung an dem Knoten; null bei
	 *         keiner Verbindung
	 */
	public static Object[] findNextKnot(Location l, List<String> besucht,
			Integer steps, UUID uuid) {
		List<String> noch_zu_prüfen = new ArrayList<String>();
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage("Suche wurde gestartet mit steps: " + steps);
		}
		return findNextKnot(l, besucht, noch_zu_prüfen, steps, uuid);
	}

	/**
	 * Hierbei müssen sString listen übergeben werden, da strings immutable sind
	 * 
	 * @param l
	 * @param besucht
	 * @param nzp
	 *            noch zu prüfende Locations
	 * @return
	 */
	public static Object[] findNextKnot(Location l, List<String> b,
			List<String> nzp, Integer steps, UUID uuid) {
		Location lc = l.clone();

		Object[] info = new Object[3];

		// Macht eine Koopie
		List<Location> besucht = new ArrayList<Location>();
		for (String s : b) {
			besucht.add(LocationSerialize.serializeToLocation(s));
		}
		List<Location> noch_zu_prüfen = new ArrayList<Location>();
		for (String s : nzp) {
			noch_zu_prüfen.add(LocationSerialize.serializeToLocation(s));
		}
		// Ende Koopie

		besucht.add(lc);

		while (Core.getInstance().isInHashMap(lc)) {
			List<Location> neighbors = getNeighbors(lc, uuid);
			info[2] = lc;
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Geprüft: " + besucht.size());
			}
			if (neighbors.size() == 0) {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Keinen Knotenpunkt gefunden");
				}
				return null;
			}

			for (Location loc : neighbors) {
				if (!besucht.contains(loc)) {
					if (!noch_zu_prüfen.contains(loc)) {
						noch_zu_prüfen.add(loc);
					}
				}
			}

			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Noch zu Prüfen : "
						+ noch_zu_prüfen.size());
			}
			if (noch_zu_prüfen.size() != 0) {
				Location zu_prüfen = noch_zu_prüfen.get(0);
				noch_zu_prüfen.remove(0);
				steps = new Integer(steps.intValue() + 1);
				if (Core.getInstance().isInKnotMap(zu_prüfen)) {
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage("Knotenpunkt gefunden! : "
								+ zu_prüfen.getX() + " | " + zu_prüfen.getY()
								+ " | " + zu_prüfen.getZ());
						Bukkit.broadcastMessage("Kosten: " + steps.intValue());
					}
					info[0] = new Location(zu_prüfen.getWorld(),
							zu_prüfen.getBlockX(), zu_prüfen.getBlockY(),
							zu_prüfen.getBlockZ());
					info[1] = steps;
					return info;
				}
				besucht.add(zu_prüfen);
				lc = zu_prüfen;
			} else {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Keinen Knotenpunkt gefunden");
				}
				return null;
			}
		}
		return null;

	}

	public static List<Location> getNeighbors(Location l, UUID uuid) {
		List<Location> back = new ArrayList<Location>();
		List<String> s_locs = DamageController.getNeighbors26(l);

		for (String s : s_locs) {
			Location temp = LocationSerialize.serializeToLocation(s);
			if (Core.getInstance().isInHashMap(temp)) {
				if (temp.getBlock().getType() != Material.LEAVES
						&& temp.getBlock().getType() != Material.LEAVES_2)
					try {
						String representant_loc = Core.getInstance()
								.getRepresentant(
										Core.getInstance().getUUIDFromHashMap(
												temp));
						String representant_player = Core.getInstance()
								.getRepresentant(uuid);

						/**
						 * Folgende abfrage wurde entfernt, da beim Mergen von
						 * Tree's die anderen Knoten in den Schnittstellen
						 * erhalten bleiben sollen. Bei CostUpdate() wird diese
						 * abfrage berücksichtigt.
						 */
						// if (representant_player
						// .equalsIgnoreCase(representant_loc)) {
						back.add(temp.clone());
						// }
					} catch (Exception e) {

					}
			}
		}
		return back;
	}

	public String toString() {
		return "Knot: "
				+ LocationSerialize.serializeToLocation(s_loc).toString();
	}

	public static Object[] findNextKnotWithLeaves(Location l, UUID uuid) {
		List<String> besucht = new ArrayList<String>();
		List<String> noch_zu_prüfen = new ArrayList<String>();
		return findNextKnotWithLeaves(l, besucht, noch_zu_prüfen,
				new Integer(0), uuid);
	}

	/**
	 * Sucht den nächten Knotenpunkt falls vorhanden
	 * 
	 * @param l
	 *            Location von der gesucht werden soll
	 * @param List
	 *            <String> besucht, besuchte Locations
	 * @param Integer
	 *            steps Anzahl benötigter schritte
	 * @param UUID
	 *            uuid, die uuid für welche Gruppe
	 * @return Object[] Object[0] = Location des Knotenpunktes Object[1] =
	 *         steps; Object[2] = Location der Richtung an dem Knoten; null bei
	 *         keiner Verbindung
	 */
	public static Object[] findNextKnotWithLeaves(Location l,
			List<String> besucht, Integer steps, UUID uuid) {
		List<String> noch_zu_prüfen = new ArrayList<String>();
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage("Suche wurde gestartet mit steps: " + steps);
		}
		return findNextKnotWithLeaves(l, besucht, noch_zu_prüfen, steps, uuid);
	}

	/**
	 * Hierbei müssen sString listen übergeben werden, da strings immutable sind
	 * 
	 * @param l
	 * @param besucht
	 * @param nzp
	 *            noch zu prüfende Locations
	 * @return
	 */
	public static Object[] findNextKnotWithLeaves(Location l, List<String> b,
			List<String> nzp, Integer steps, UUID uuid) {
		Location lc = l.clone();

		Object[] info = new Object[3];

		// Macht eine Koopie
		List<Location> besucht = new ArrayList<Location>();
		for (String s : b) {
			besucht.add(LocationSerialize.serializeToLocation(s));
		}
		List<Location> noch_zu_prüfen = new ArrayList<Location>();
		for (String s : nzp) {
			noch_zu_prüfen.add(LocationSerialize.serializeToLocation(s));
		}
		// Ende Koopie

		besucht.add(lc);

		while (Core.getInstance().isInHashMap(lc)) {
			List<Location> neighbors = getNeighborsWithLeaves(lc, uuid);
			info[2] = lc;
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Geprüft: " + besucht.size());
			}
			if (neighbors.size() == 0) {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Keinen Knotenpunkt gefunden");
				}
				return null;
			}

			for (Location loc : neighbors) {
				if (!besucht.contains(loc)) {
					if (!noch_zu_prüfen.contains(loc)) {
						noch_zu_prüfen.add(loc);
					}
				}
			}

			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Noch zu Prüfen : "
						+ noch_zu_prüfen.size());
			}
			if (noch_zu_prüfen.size() != 0) {
				Location zu_prüfen = noch_zu_prüfen.get(0);
				noch_zu_prüfen.remove(0);
				steps = new Integer(steps.intValue() + 1);
				if (Core.getInstance().isInKnotMap(zu_prüfen)) {
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage("Knotenpunkt gefunden! : "
								+ zu_prüfen.getX() + " | " + zu_prüfen.getY()
								+ " | " + zu_prüfen.getZ());
						Bukkit.broadcastMessage("Kosten: " + steps.intValue());
					}
					info[0] = new Location(zu_prüfen.getWorld(),
							zu_prüfen.getBlockX(), zu_prüfen.getBlockY(),
							zu_prüfen.getBlockZ());
					info[1] = steps;
					return info;
				}
				besucht.add(zu_prüfen);
				lc = zu_prüfen;
			} else {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Keinen Knotenpunkt gefunden");
				}
				return null;
			}
		}
		return null;

	}

	public static List<Location> getNeighborsWithLeaves(Location l, UUID uuid) {
		List<Location> back = new ArrayList<Location>();
		List<String> s_locs = DamageController.getNeighbors26(l);

		for (String s : s_locs) {
			Location temp = LocationSerialize.serializeToLocation(s);
			if (Core.getInstance().isInHashMap(temp)) {
				// if (temp.getBlock().getType() != Material.LEAVES
				// && temp.getBlock().getType() != Material.LEAVES_2){
				try {
					String representant_loc = Core
							.getInstance()
							.getRepresentant(
									Core.getInstance().getUUIDFromHashMap(temp));
					String representant_player = Core.getInstance()
							.getRepresentant(uuid);
					if (representant_player.equalsIgnoreCase(representant_loc)) {
						back.add(temp.clone());
					}
				} catch (Exception e) {

				}
				// }
			}
		}
		return back;
	}

}
