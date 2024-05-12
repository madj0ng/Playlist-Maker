import com.example.playlistmaker.util.FormatUtils
import com.example.playlistmaker.util.HandlerUtils
import org.koin.dsl.module

val utilModule = module {
    single {
        HandlerUtils
    }

    single{
        FormatUtils
    }
}