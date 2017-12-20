#include <locale.h>
int main(int argc, char **argv) {
  setlocale(LC_ALL, "");
  struct lconv *cur_locale = localeconv();
  if (atoi(argv[1])) 
  {
    printf("%s", cur_locale->decimal_point);
  } 
  else 
  {
    printf("%s", cur_locale->currency_symbol);
  }
}
