

class checker {
  public static void main(String[] args) {
    int dots = 0;

    for (int i = 0; i < args[0].length(); ++i) {
      if (args[0].charAt(i) == '.') {
        ++dots; System.out.println("ORBS: " + dots);



      }
    }


  }
}
