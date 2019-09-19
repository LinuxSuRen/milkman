package milkman.utils.fxml;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.Property;
 
public class GenericBinding<O, T> extends ObjectPropertyBase<T> {
   private final Function<O, T> getter;
   private final BiConsumer<O, T> setter;
 
   private O obj;
 
   
   public static <OS, TS> GenericBinding<OS, TS> of(Function<OS, TS> getter, BiConsumer<OS, TS> setter){
	   return new GenericBinding<>(getter, setter);
   }
   
   public static <OS, TS> GenericBinding<OS, TS> of(Function<OS, TS> getter, BiConsumer<OS, TS> setter, OS obj){
	   GenericBinding<OS, TS> b = new GenericBinding<>(getter, setter);
	   b.obj = obj;
	   return b;
   }
   
   
    private GenericBinding(Function<O, T> getter, BiConsumer<O, T> setter) {
        super();
		this.getter = getter;
		this.setter = setter;
    }
    
    @Override
    public void set(T v) {
        try {
            fireValueChangedEvent();
            setter.accept(obj, v);
            super.set(v);
        } catch (final Throwable t) {
            throw new RuntimeException("Unable to set value: " + v, t);
        }
    };
    @Override
    public T get() {
        try {
            // TODO : here we are lazily loading the property which will prevent any property listeners
            // from receiving notice of a direct model field change until the next time the get method
            // is called on the PathProperty
            final T prop = getter.apply(obj);
            if (!Objects.equals(super.get(),prop)) {
                super.set(prop);
            }
            return super.get();
        } catch (final Throwable t) {
            throw new RuntimeException("Unable to get value", t);
        }
    }
    
    
    public void bindTo(Property<T> property, O obj) {
    	this.obj = obj;
    	property.unbindBidirectional(this);
    	property.bindBidirectional(this);
    }
    
	@Override
	public Object getBean() {
		return null;
	}

	@Override
	public String getName() {
		return "";
	}
	
	
	public EventStream<T> toStream(){
		//this instead of EventStream.nonNullValues bc we want to omit initial (artificial) value
		return EventStreams.changesOf(this).filterMap(c -> Optional.ofNullable(c.getNewValue()));
	}

}