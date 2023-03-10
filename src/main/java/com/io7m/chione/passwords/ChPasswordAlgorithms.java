/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.chione.passwords;

import java.util.List;
import java.util.Objects;

/**
 * Functions over password algorithms.
 */

public final class ChPasswordAlgorithms
{
  private ChPasswordAlgorithms()
  {

  }

  /**
   * Parse a password algorithm identifier (such as
   * "PBKDF2WithHmacSHA256:10000:256").
   *
   * @param text The identifier
   *
   * @return A password algorithm
   *
   * @throws ChPasswordException On parse errors
   * @see ChPasswordAlgorithmType#identifier()
   */

  public static ChPasswordAlgorithmType parse(
    final String text)
    throws ChPasswordException
  {
    Objects.requireNonNull(text, "text");

    final var segments = List.of(text.split(":"));

    final var name = segments.get(0);
    return switch (name) {
      case "REDACTED" -> {
        yield ChPasswordAlgorithmRedacted.create();
      }

      case "PBKDF2WithHmacSHA256" -> {
        try {
          if (segments.size() == 3) {
            yield ChPasswordAlgorithmPBKDF2HmacSHA256.create(
              Integer.parseUnsignedInt(segments.get(1)),
              Integer.parseUnsignedInt(segments.get(2))
            );
          }

          final var lineSeparator = System.lineSeparator();
          throw new ChPasswordException(
            new StringBuilder(128)
              .append("Unparseable password algorithm.")
              .append(lineSeparator)
              .append(
                "  Expected: 'PBKDF2WithHmacSHA256' : <iteration count> : <key length>")
              .append(lineSeparator)
              .append("  Received: ")
              .append(text)
              .append(lineSeparator)
              .toString()
          );
        } catch (final NumberFormatException e) {
          throw new ChPasswordException(e.getMessage(), e);
        }
      }

      default -> {
        throw new ChPasswordException(
          "Unsupported algorithm: %s".formatted(name)
        );
      }
    };
  }
}
