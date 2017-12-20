




#include <locale.h>

int main(int argc, char **argv) {

  struct lconv *cur_locale = localeconv();
  if (atoi(argv[1])) 
  {
    printf("%s\n", cur_locale->decimal_point);




  }

}
