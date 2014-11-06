package demo;

/**
 * Created by taozhiheng on 14-11-5.
 */
    public class Decorator implements Component{
        private Component component;

        public Decorator(Component component){
            this.component = component;
        }

        @Override
        public String change() {
            // 委派给构件
           return component.change();
        }

    }