/*
 * Copyright (C) 2015 José Paumard
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.davidmoten.kool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class Util {

    private Util() { }

    public static Set<String> readScrabbleWords() {
        Set<String> scrabbleWords = new HashSet<>() ;
        try (java.util.stream.Stream<String> scrabbleWordsStream = Files.lines(new File("src/test/resources/ospd.txt").toPath())) {
            scrabbleWords.addAll(scrabbleWordsStream.map(String::toLowerCase).collect(Collectors.toSet()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return scrabbleWords;
    }

    public static Set<String> readShakespeareWords() {
        Set<String> shakespeareWords = new HashSet<>() ;
        try (java.util.stream.Stream<String> shakespeareWordsStream = Files.lines(new File("src/test/resources/words.shakespeare.txt").toPath())) {
            shakespeareWords.addAll(shakespeareWordsStream.map(String::toLowerCase).collect(Collectors.toSet()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return shakespeareWords ;
    }
}