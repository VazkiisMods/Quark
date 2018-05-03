package vazkii.quark.vanity.client.emotes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.minecraft.client.model.ModelBiped;
import vazkii.aurelienribon.tweenengine.Timeline;
import vazkii.aurelienribon.tweenengine.Tween;
import vazkii.aurelienribon.tweenengine.TweenEquation;
import vazkii.aurelienribon.tweenengine.TweenEquations;
import vazkii.quark.base.lib.LibMisc;

public class EmoteTemplate {

	private static final Map<String, Integer> parts = new HashMap();
	private static final Map<String, Integer> tweenables = new HashMap();
	private static final Map<String, Function> functions = new HashMap();
	private static final Map<String, TweenEquation> equations = new HashMap();

	static {
		functions.put("name", EmoteTemplate::name);
		functions.put("use", EmoteTemplate::use);
		functions.put("unit", EmoteTemplate::unit);
		functions.put("animation", EmoteTemplate::animation);
		functions.put("section", EmoteTemplate::section);
		functions.put("end", EmoteTemplate::end);
		functions.put("move", EmoteTemplate::move);
		functions.put("reset", EmoteTemplate::reset);
		functions.put("pause", EmoteTemplate::pause);
		functions.put("yoyo", EmoteTemplate::yoyo);
		functions.put("repeat", EmoteTemplate::repeat);
		

		Class<?> clazz = ModelAccessor.class;
		Field[] fields = clazz.getDeclaredFields();
		for(Field f : fields) {
			if(f.getType() != int.class)
				continue;
			
			int modifiers = f.getModifiers();
			if(Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
				try {
					int val = f.getInt(null);
					String name = f.getName().toLowerCase();
					if(name.matches("^.+?_[xyz]$"))
						tweenables.put(name, val);
					else
						parts.put(name, val);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		clazz = TweenEquations.class;
		fields = clazz.getDeclaredFields();
		for(Field f : fields) {
			String name = f.getName().replaceAll("[A-Z]", "_$0").substring(5).toLowerCase();
			try {
				TweenEquation eq = (TweenEquation) f.get(null);
				equations.put(name, eq);
				if(name.equals("none"))
					equations.put("linear", eq);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	final String file;
	
	List<String> readLines;
	List<Integer> usedParts;
	Stack<Timeline> timelineStack;
	float speed;
	boolean compiled = false;
	boolean compiledOnce = false;
	
	public EmoteTemplate(String file) {
		this.file = file;
		readAndMakeTimeline(null);
	}

	public Timeline getTimeline(ModelBiped model) {
		compiled = false;
		speed = 1;

		if(readLines == null)
			return readAndMakeTimeline(model);
		else {
			Timeline timeline = null;
			timelineStack = new Stack();

			int i = 0;
			try {
				for(; i < readLines.size() && !compiled; i++)
					timeline = handle(model, timeline, readLines.get(i));
			} catch(Exception e) {
				logError(e, i);
				return Timeline.createSequence();
			}
			
			if(timeline == null) 
				return Timeline.createSequence();
			
			return timeline;
		}
	}
	
	public Timeline readAndMakeTimeline(ModelBiped model) {
		Timeline timeline = null;
		usedParts = new ArrayList();
		timelineStack = new Stack();
		int lines = 0;
		
		BufferedReader reader = null;
		compiled = compiledOnce = false;
		readLines = new ArrayList();
		try {
			reader = createReader();

			try {
				String s;
				while((s = reader.readLine()) != null && !compiled) {
					lines++;
					readLines.add(s);
					timeline = handle(model, timeline, s);
				}
			} catch(Exception e) {
				logError(e, lines);
				return fallback();
			}

			if(timeline == null) 
				return fallback();

			return timeline;
		} catch(IOException e) {
			e.printStackTrace();
			return fallback();
		} finally {
			compiledOnce = true;
			try {
				if(reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	BufferedReader createReader() throws FileNotFoundException {
		return new BufferedReader(new InputStreamReader(EmoteTemplate.class.getResourceAsStream("/assets/quark/emotes/" + file)));
	}
	
	Timeline fallback() {
		return Timeline.createSequence();
	}
	
	private void logError(Exception e, int line) {
		LibMisc.LOGGER.error("[Custom Emotes] Error loading line " + (line + 1) + " of emote " + file);
		if(!(e instanceof IllegalArgumentException)) {
			LibMisc.LOGGER.error("[Custom Emotes] This is an Internal Error, and not one in the emote file, please report it");
			e.printStackTrace();
		}
		else LibMisc.LOGGER.error("[Custom Emotes] " + e.getMessage());
	}

	private Timeline handle(ModelBiped model, Timeline timeline, String s) throws IllegalArgumentException {
		s = s.trim();
		if(s.startsWith("#") || s.isEmpty())
			return timeline;
		
		String[] tokens = s.trim().split(" ");
		String function = tokens[0];
		
		if(functions.containsKey(function))
			return functions.get(function).invoke(this, model, timeline, tokens);

		throw new IllegalArgumentException("Illegal function name " + function);
	}
	
	void setName(String[] tokens) { }
	
	private static Timeline name(EmoteTemplate em, ModelBiped model, Timeline timeline, String[] tokens) throws IllegalArgumentException {
		em.setName(tokens);
		return timeline;
	}

	private static Timeline use(EmoteTemplate em, ModelBiped model, Timeline timeline, String[] tokens) throws IllegalArgumentException {
		if(em.compiledOnce)
			return timeline;
		
		assertParamSize(tokens, 2);

		String part = tokens[1];

		if(parts.containsKey(part))
			em.usedParts.add(parts.get(part));
		else throw new IllegalArgumentException("Illgal part name for function use: " + part);

		return timeline;
	}

	private static Timeline unit(EmoteTemplate em, ModelBiped model, Timeline timeline, String[] tokens) throws IllegalArgumentException {
		assertParamSize(tokens, 2);
		em.speed = Float.parseFloat(tokens[1]);
		return timeline;
	}
	
	private static Timeline animation(EmoteTemplate em, ModelBiped model, Timeline timeline, String[] tokens) throws IllegalArgumentException {
		if(timeline != null)
			throw new IllegalArgumentException("Illegal use of function animation, animation already started");

		assertParamSize(tokens, 2);

		String type = tokens[1];
		switch(type) {
		case "sequence":
			return Timeline.createSequence();
		case "parallel":
			return Timeline.createParallel();
		default: throw new IllegalArgumentException("Illegal animation type: " + type);
		}
	}

	private static Timeline section(EmoteTemplate em, ModelBiped model, Timeline timeline, String[] tokens) throws IllegalArgumentException {
		assertParamSize(tokens, 2);

		String type = tokens[1];
		Timeline newTimeline;
		switch(type) {
		case "sequence":
			newTimeline = Timeline.createSequence();
			break;
		case "parallel":
			newTimeline = Timeline.createParallel();
			break;
		default: throw new IllegalArgumentException("Illegal section type: " + type);
		}

		em.timelineStack.push(timeline);
		return newTimeline;
	}

	private static Timeline end(EmoteTemplate em, ModelBiped model, Timeline timeline, String[] tokens) throws IllegalArgumentException {
		assertParamSize(tokens, 1);

		if(em.timelineStack.isEmpty()) {
			em.compiled = true;
			return timeline;
		}

		Timeline poppedLine = em.timelineStack.pop();
		poppedLine.push(timeline);
		return poppedLine;
	}

	private static Timeline move(EmoteTemplate em, ModelBiped model, Timeline timeline, String[] tokens) throws IllegalArgumentException {
		if(tokens.length < 4)
			throw new IllegalArgumentException(String.format("Illgal parameter amount for function move: %d (at least 4 are required)", tokens.length));

		String partStr = tokens[1];
		int part;
		if(tweenables.containsKey(partStr))
			part = tweenables.get(partStr);
		else throw new IllegalArgumentException("Illgal part name for function move: " + partStr);
		
		float time = Float.parseFloat(tokens[2]) * em.speed;
		float target = Float.parseFloat(tokens[3]);

		Tween tween = null;
		boolean valid = model != null;
		if(valid)
			tween = Tween.to(model, part, time).target((float) target);
		if(tokens.length > 4) {
			int index = 4;
			while(index < tokens.length) {
				String cmd = tokens[index++];
				int times;
				float delay;
				switch(cmd) {
				case "delay":
					assertParamSize("delay", tokens, 1, index);
					delay = Float.parseFloat(tokens[index++]) * em.speed;
					if(valid)
						tween = tween.delay(delay);
					break;
				case "yoyo":
					assertParamSize("yoyo", tokens, 2, index);
					times = Integer.parseInt(tokens[index++]);
					delay = Float.parseFloat(tokens[index++]) * em.speed;
					if(valid)
						tween = tween.repeatYoyo(times, delay);
					break;
				case "repeat":
					assertParamSize("repeat", tokens, 2, index);
					times = Integer.parseInt(tokens[index++]);
					delay = Float.parseFloat(tokens[index++]) * em.speed;
					if(valid)
						tween = tween.repeat(times, delay);
					break;
				case "ease":
					assertParamSize("ease", tokens, 1, index);
					String easeType = tokens[index++];
					if(equations.containsKey(easeType)) {
						if(valid)
							tween = tween.ease(equations.get(easeType));
					} else throw new IllegalArgumentException("Easing type " + easeType + " doesn't exist");
					break;
				default:
					throw new IllegalArgumentException(String.format("Invalid modifier %s for move function", cmd));
				}
			}
		}

		if(valid)
			return timeline.push(tween);
		return timeline;
	}
	
	private static Timeline reset(EmoteTemplate em, ModelBiped model, Timeline timeline, String[] tokens) throws IllegalArgumentException {
		if(tokens.length < 4)
			throw new IllegalArgumentException(String.format("Illgal parameter amount for function reset: %d (at least 4 are required)", tokens.length));

		String part = tokens[1];
		boolean allParts = part.equals("all");
		if(!allParts && !parts.containsKey(part))
			throw new IllegalArgumentException("Illgal part name for function reset: " + part);
		
		String type = tokens[2];
		boolean all = type.equals("all");
		boolean rot = all || type.equals("rotation");
		boolean off = all || type.equals("offset");
		
		if(!rot && !off)
			throw new IllegalArgumentException("Illgal reset type: " + type);
		
		int partInt = allParts ? 0 : parts.get(part);
		float time = Float.parseFloat(tokens[3]) * em.speed;
		
		if(model != null) {
			Timeline parallel = Timeline.createParallel();
			int lower = allParts ? 0 : partInt + (rot ? 0 : 3);
			int upper = allParts ? ModelAccessor.STATE_COUNT : partInt + (off ? 6 : 3);
			
			for(int i = lower; i < upper; i++) {
				int piece = i / ModelAccessor.MODEL_PROPS * ModelAccessor.MODEL_PROPS;
				if(em.usedParts.contains(piece))
					parallel.push(Tween.to(model, i, time));
			}
			
			timeline.push(parallel);
		}
		
		return timeline;
	}
	

	private static Timeline pause(EmoteTemplate em, ModelBiped model, Timeline timeline, String[] tokens) throws IllegalArgumentException {
		assertParamSize(tokens, 2);
		float ms = Float.parseFloat(tokens[1]) * em.speed;
		return timeline.pushPause(ms);
	}

	private static Timeline yoyo(EmoteTemplate em, ModelBiped model, Timeline timeline, String[] tokens) throws IllegalArgumentException {
		assertParamSize(tokens, 3);
		int times = Integer.parseInt(tokens[1]);
		float delay = Float.parseFloat(tokens[2]) * em.speed;
		return timeline.repeatYoyo(times, delay);
	}

	private static Timeline repeat(EmoteTemplate em, ModelBiped model, Timeline timeline, String[] tokens) throws IllegalArgumentException {
		assertParamSize(tokens, 3);
		int times = Integer.parseInt(tokens[1]);
		float delay = Float.parseFloat(tokens[2]) * em.speed;
		return timeline.repeat(times, delay);
	}
	
	private static void assertParamSize(String[] tokens, int expect) throws IllegalArgumentException {
		if(tokens.length != expect)
			throw new IllegalArgumentException(String.format("Illgal parameter amount for function %s: %d (expected %d)", tokens[0], tokens.length, expect));
	}

	private static void assertParamSize(String mod, String[] tokens, int expect, int startingFrom) throws IllegalArgumentException {
		if(tokens.length - startingFrom < expect)
			throw new IllegalArgumentException(String.format("Illgal parameter amount for move modifier %s: %d (expected at least %d)", mod, tokens.length, expect));
	}

	public boolean usesBodyPart(int part) {
		return usedParts.contains(part);
	}

	private static interface Function {
		Timeline invoke(EmoteTemplate em, ModelBiped model, Timeline timeline, String[] tokens) throws IllegalArgumentException;
	}

}
