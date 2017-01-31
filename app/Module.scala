import com.google.inject.AbstractModule
import java.time.Clock

import services._

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[ProfitCalculatorService]).to(classOf[ProfitCalculator])
    bind(classOf[FixedProfitService]).to(classOf[ConfigurationFixedProfitService])
    bind(classOf[ProfitTableRepository]).to(classOf[InMemoryProfitTableRepository])
  }

}
