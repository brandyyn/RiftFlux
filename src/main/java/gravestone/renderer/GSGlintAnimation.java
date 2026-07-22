package gravestone.renderer;

public final class GSGlintAnimation {
   private static final long START_NANOS = System.nanoTime();

   private GSGlintAnimation() {
   }

   public static double getElapsedSeconds() {
      return (double)(System.nanoTime() - START_NANOS) / 1000000000.0D;
   }
}
