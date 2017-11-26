#include <stdlib.h>
#include <stdio.h>
#include <locale.h>

int main(int argc, char **argv) {
  setlocale(LC_ALL, "");
  struct lconv *cur_locale = localeconv();
  if (atoi(argv[1])) 
  {
    printf("%s\n", cur_locale->decimal_point);
  } 
  else 
  {
    printf("%s\n", cur_locale->currency_symbol);
  }
  return 0;
}
