package gravestone.block;

/** Authorizes narrowly scoped Gravestone-owned block removals. */
public final class GravestoneRemovalGuard {
   private static final ThreadLocal<Integer> AUTHORIZATION_DEPTH = new ThreadLocal<Integer>() {
      @Override
      protected Integer initialValue() {
         return 0;
      }
   };

   private GravestoneRemovalGuard() {
   }

   public static void beginAuthorizedRemoval() {
      AUTHORIZATION_DEPTH.set(AUTHORIZATION_DEPTH.get() + 1);
   }

   public static void endAuthorizedRemoval() {
      int depth = AUTHORIZATION_DEPTH.get() - 1;
      if (depth <= 0) {
         AUTHORIZATION_DEPTH.remove();
      } else {
         AUTHORIZATION_DEPTH.set(depth);
      }
   }

   public static boolean isRemovalAuthorized() {
      return AUTHORIZATION_DEPTH.get() > 0;
   }
}
