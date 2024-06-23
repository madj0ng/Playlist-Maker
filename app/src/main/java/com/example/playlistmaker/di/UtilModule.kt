import com.example.playlistmaker.util.DebounceUtils
import com.example.playlistmaker.util.FormatUtils
import org.koin.dsl.module

val utilModule = module {
    single {
        DebounceUtils
    }

    single {
        FormatUtils
    }
}