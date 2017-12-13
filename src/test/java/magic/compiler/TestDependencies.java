package magic.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import magic.Core;
import magic.data.Symbol;
import magic.lang.Context;
import magic.lang.Slot;

public class TestDependencies {
	@Test public void testIfDependencies() {
		EvalResult<?> r=Core.eval("(def a (if b c d))");
		Context c=r.getContext();
		Slot<?> slot=c.getSlot("magic.core/a");
		assertNotNull(slot);
		assertEquals(Symbol.createSet("magic.core/if","magic.core/b","magic.core/c","magic.core/d"),slot.getDependencies());
	}
	
	@Test public void testFnDependencies() {
		EvalResult<?> r=Core.eval("(defn f [a] (if b (inc a) (dec a)))");
		Context c=r.getContext();
		Slot<?> slot=c.getSlot("magic.core/f");
		assertNotNull(slot);
		assertEquals(Symbol.createSet("magic.core/fn","magic.core/if","magic.core/b","magic.core/inc","magic.core/dec"),slot.getDependencies());
	}
	
	@Test public void testApplyDependencies() {
		EvalResult<?> r=Core.eval("(def a (nil? b))");
		Context c=r.getContext();
		Slot<?> slot=c.getSlot("magic.core/a");
		assertNotNull(slot);
		assertEquals(Symbol.createSet("magic.core/nil?","magic.core/b"),slot.getDependencies());
	}
	
	@Test public void testLoopDependencies() {
		EvalResult<?> r=Core.eval("(def a (loop [acc 1 i b] (if (<= i 1) acc (recur (* acc i) (dec i)))))");
		Context c=r.getContext();
		Slot<?> slot=c.getSlot("magic.core/a");
		assertNotNull(slot);
		assertEquals(Symbol.createSet("magic.core/if","magic.core/b","magic.core/dec","magic.core/*","magic.core/<=","magic.core/loop","magic.core/recur"),slot.getDependencies());
	}
}
