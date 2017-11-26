class checker {
  public static void main(String[] args) {
    int dots = 0;
    int chars = 0;
    for (int i = 0; i < args[0].length(); ++i) {
      if (args[0].charAt(i) == '.') {
        ++dots;
      } else if ((args[0].charAt(i) >= '0') 
                 && (args[0].charAt(i) <= '9')) {
        ++chars;
      }
    }
    System.out.println(dots);
    System.out.println(chars);
  }
}
